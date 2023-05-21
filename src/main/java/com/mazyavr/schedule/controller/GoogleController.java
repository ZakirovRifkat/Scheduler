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
import com.mazyavr.schedule.entity.TaskEntity;
import com.mazyavr.schedule.repository.ProjectRepository;
import com.mazyavr.schedule.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name="Google контроллер", description="Контроллер для интеграции с google-календарем")
@Controller
@RequestMapping(path = "/google")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
class GoogleController {
    final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    Calendar service = null;
    String token;

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectRepository projectRepository;

    private void initService() {
        if (service != null) {
            return;
        }

        if (token == null) {
            throw new RuntimeException("Unable to initialize google calendar");
        }

        try {
            var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            var credential = new GoogleCredential().setAccessToken(token);

            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("DownScheduler")
                    .build();
        } catch (IOException | GeneralSecurityException e) {

        }

        if (service == null) {
            throw new RuntimeException("Unable to initialize google calendar");
        }
    }

    @Operation(
        summary = "Ручка обратного вызова OAuth",
        description = "Обменивает код, полученный при авторизации OAuth, на токен, который можно использовать для запросов к API гугла"
    )
    @GetMapping(value = "/callback")
    public RedirectView googleCallback(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "state") String redirectUri
    ) {
        try {
            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(), new GsonFactory(),
                    "530835646351-odkl0qt3l07n6s7httrpsd6rd37ka5l9.apps.googleusercontent.com", "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",
                    code, "http://localhost:8080/google/callback")
                    .execute();

            this.token = response.getAccessToken();
        } catch (IOException e) {
        }

        return new RedirectView(redirectUri);
    }

    @Operation(
        summary = "Сообщает, авторизовался ли уже пользователь через OAuth"
    )
    @GetMapping(value = "/is-authorized")
    public @ResponseBody IsAuthorizedResponse isAuthorized() {
        return new IsAuthorizedResponse(token != null);
    }

    @Operation(
        summary = "Получение событий из google-календаря"
    )
    @PostMapping(value = "/download")
    public @ResponseBody SimpleResponse getEvents(
            @RequestParam(value = "projectId") long projectId
    ) throws IOException {
        initService();

        var projectO = projectRepository.findById(projectId);
        if (projectO.isEmpty()) {
            throw new IllegalArgumentException("No such project");
        }

        var project = projectO.get();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
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
                task.setDescription(description == null ? "" : description.length() > 254 ? description.substring(0, 254) : description);
                task.setStart(startDateTime);
                task.setEnd(endDateTime);
                task.setStatus(false);
                task.setProject(project);

                taskRepository.save(task);
            }
        }

        return new SimpleResponse();
    }

    @Operation(
        summary = "Передача событий google-календарю"
    )
    @PostMapping(value = "/upload")
    public @ResponseBody SimpleResponse setEvents(
            @RequestParam(value = "projectId") long projectId
    ) throws IOException {
        initService();

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
            event = service.events().insert(calendarId, event).execute();
        }
        return new SimpleResponse();
    }

    private record IsAuthorizedResponse(boolean authorized) {
    }
}