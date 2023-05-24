package com.mazyavr.schedule.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.dto.UserDto;
import com.mazyavr.schedule.entity.TaskEntity;
import com.mazyavr.schedule.entity.UserEntity;
import com.mazyavr.schedule.repository.ProjectRepository;
import com.mazyavr.schedule.repository.TaskRepository;
import com.mazyavr.schedule.repository.UserRepository;
import com.mazyavr.schedule.service.UserService;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder.In;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

  final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private TaskRepository taskRepository;

  private String token;
  private Integer lifespan;

  private Calendar initCalendar(long userId) {

    Calendar userCalendar = null;

    String userToken = userRepository.findById(userId).get().getToken();

    if (userToken == null) {
      throw new RuntimeException("Unable to initialize google calendar");
    }

    try {
      var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      var credential = new GoogleCredential().setAccessToken(userToken);

      userCalendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
          .setApplicationName("DownScheduler")
          .build();
    } catch (IOException | GeneralSecurityException e) {

    }

    if (userCalendar == null) {
      throw new RuntimeException("Unable to initialize google calendar");
    }
    return userCalendar;
  }

  /**
   * Здесь реализована только регистрация(добавление в БД), но не авторизация(обновление юзверя в
   * БД)
   */
  @PostMapping(path = "/login")
  public @ResponseBody RedirectView login(
      @RequestParam(value = "code") String code,
      @RequestParam(value = "state") String redirectUri,
      HttpServletResponse servletResponse
  ) {
    try {
      GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
          new NetHttpTransport(), new GsonFactory(),
          "530835646351-odkl0qt3l07n6s7httrpsd6rd37ka5l9.apps.googleusercontent.com",
          "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",
          code, "http://localhost:8080/users/login")
          .execute();
      this.token = response.getAccessToken();
      this.lifespan = Math.toIntExact(response.getExpiresInSeconds());

      //Здесь должна быть проверка на наличие пользователя в БД, но пока сделаем просто регистрацию для всех
      UserEntity user = new UserEntity();
      user.setToken(token);
      //user.setEmail(user.getId());
      userRepository.save(user);
      Cookie cookie = new Cookie("userId", Long.toString(user.getId()));
      cookie.setPath("/");
      cookie.setMaxAge(86400);
      servletResponse.addCookie(cookie);
    } catch (IOException e) {
    }

    return new RedirectView(redirectUri);
  }

  @GetMapping(path = "/logout")
  public @ResponseBody SimpleResponse logout(HttpServletResponse response) throws IOException {

    Cookie cookie = new Cookie("userId", null);
    cookie.setMaxAge(0);
    response.addCookie(cookie);

    return new SimpleResponse();
  }

  @GetMapping(value = "/is-authorized")
  public @ResponseBody IsAuthorizedResponse isAuthorized(
      @CookieValue(value = "userId") String userId) {
    return new IsAuthorizedResponse(userId != null);
  }

  private record IsAuthorizedResponse(boolean authorized) {

  }

  @PostMapping(value = "/download")
  public @ResponseBody SimpleResponse getEvents(
      @RequestParam(value = "projectId") long projectId,
      @RequestParam(value = "userId") long userId
  ) throws IOException {
    Calendar userCalendar = initCalendar(userId);

    var projectO = projectRepository.findById(projectId);
    if (projectO.isEmpty()) {
      throw new IllegalArgumentException("No such project");
    }

    var project = projectO.get();

    DateTime now = new DateTime(System.currentTimeMillis());
    Events events = userCalendar.events().list("primary")
//                .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute();
    List<Event> items = events.getItems();
    if (items.isEmpty()) {
      System.out.println("No upcoming events found.");
    } else {
      System.out.println("Upcoming events");
      for (Event event : items) {
        DateTime start = event.getStart().getDateTime();
        if (start == null) {
          start = event.getStart().getDate();
        }
        String description = event.getDescription();
        String summary = event.getSummary();
        DateTime end = event.getEnd().getDateTime();
        if (end == null) {
          end = event.getEnd().getDate();
        }

        ZonedDateTime startDateTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(start.getValue()),
            ZoneId.systemDefault()
        );
        ZonedDateTime endDateTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(start.getValue()),
            ZoneId.systemDefault()
        );

        TaskEntity task = new TaskEntity();
        task.setName(summary == null ? "" : summary);
        task.setDescription(description == null ? ""
            : description.length() > 254 ? description.substring(0, 254) : description);
        task.setStart(startDateTime);
        task.setEnd(endDateTime);
        task.setStatus(false);
        task.setProject(project);

        taskRepository.save(task);
      }
    }

    return new SimpleResponse();
  }

  @PostMapping(value = "/upload")
  public @ResponseBody SimpleResponse setEvents(
      @RequestParam(value = "projectId") long projectId,
      @RequestParam(value = "userId") long userId
  ) throws IOException {
    Calendar userCalendar = initCalendar(userId);

    List<TaskEntity> tasks = new ArrayList<>();

    for (TaskEntity t : taskRepository.findAll()) {
      if (t.getProject().getId() == projectId) {
        tasks.add(t);
      }
    }

    for (var t : tasks) {
      Event event = new Event()
          .setSummary(t.getName())
          .setDescription(t.getDescription());

      DateTime startDateTime = new DateTime(t.getStart().toEpochSecond() * 1000);
      EventDateTime start = new EventDateTime()
          .setDateTime(startDateTime);
      event.setStart(start);

      DateTime endDateTime = new DateTime(t.getEnd().toEpochSecond() * 1000);
      EventDateTime end = new EventDateTime()
          .setDateTime(endDateTime);
      event.setEnd(end);

      String calendarId = "primary";
      event = userCalendar.events().insert(calendarId, event).execute();
    }
    return new SimpleResponse();
  }
}
