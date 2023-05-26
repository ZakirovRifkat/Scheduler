package com.mazyavr.schedule.controller;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.entity.TaskEntity;
import com.mazyavr.schedule.entity.UserEntity;
import com.mazyavr.schedule.repository.ProjectRepository;
import com.mazyavr.schedule.repository.TaskRepository;
import com.mazyavr.schedule.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

  final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  String CLIENT_ID = "530835646351-odkl0qt3l07n6s7httrpsd6rd37ka5l9.apps.googleusercontent.com";
  String CLIENT_SECRET = "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";
  String REDIRECT_URI = "http://localhost:8080/users/login";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private TaskRepository taskRepository;

  private Calendar initCalendar(long userId) {

    String refreshToken = userRepository.findById(userId).get().getRefreshToken();

    if (refreshToken == null) {
      throw new RuntimeException("Unable to initialize google calendar");
    }
  
    try {
      var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      
      var response = new GoogleRefreshTokenRequest(
        HTTP_TRANSPORT, JSON_FACTORY, refreshToken,
        CLIENT_ID, CLIENT_SECRET
      ).execute();
      
      var accessToken = response.getAccessToken();
      
      var credential = new GoogleCredential().setAccessToken(accessToken);
      
      return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName("DownScheduler")
        .build();
    } catch (IOException | GeneralSecurityException e) {
      throw new RuntimeException("Unable to initialize google calendar");
    }
  }
  
  @PostMapping(path = "/login")
  public @ResponseBody RedirectView login(
      @RequestParam(value = "code") String code,
      @RequestParam(value = "state") String redirectUri,
      HttpServletResponse servletResponse
  ) {
    try {
      var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      
      GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
        HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID,
        CLIENT_SECRET, code, REDIRECT_URI)
        .execute();
      
      var idToken = response.getIdToken();
      var verifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
        .setAudience(Collections.singletonList(CLIENT_ID)).build();
  
      var verifiedToken = verifier.verify(idToken);
  
      if (verifiedToken == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user ID provided");
      }
  
      var googleId = verifiedToken.getPayload().getSubject();
  
      UserEntity user = null;
      
      for (UserEntity t : userRepository.findAll()) {
        if (t.getGoogleId() == googleId) {
          user = t;
        }
      }
      
      if (user == null){
        user = new UserEntity();
        user.setRefreshToken(response.getRefreshToken());
        user.setGoogleId(googleId);
        userRepository.save(user);
      }
      
      Cookie cookie = new Cookie("userId", Long.toString(user.getId()));
      cookie.setPath("/");
      cookie.setMaxAge(604800);
      servletResponse.addCookie(cookie);
      } catch(IOException | GeneralSecurityException e){
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
