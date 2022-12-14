package com.mazyavr.schedule.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
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
import com.mazyavr.schedule.repository.ProjectRepository;
import com.mazyavr.schedule.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
class GoogleController {
    final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    Calendar service = null;
    String token;

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectRepository projectRepository;

    void initService() {
        if (service != null) {
            return;
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

    @RequestMapping(value = "/google/callback")
    public RedirectView googleCallback(
            @RequestParam(value = "token") String token
    ) {
        this.token = token;
        return new RedirectView("http://localhost:3000");
    }

    @RequestMapping(value = "/from-google")
    public @ResponseBody SimpleResponse getEvents(
            @RequestParam(value = "projectId") long projectId
    ) {
        initService();

        var projectO = projectRepository.findById(projectId);

        if (projectO.isEmpty()) {
            throw new IllegalArgumentException("No such project");
        }

        try {
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list("primary")
                    .setTimeMin(now)
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
                    DateTime end = event.getEnd().getDateTime();
                    if (end == null) {
                        end = event.getEnd().getDate();
                    }

                    //System.out.printf("%s (%s)\n", event.getSummary(), start);
                    ZonedDateTime startDateTime = ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(start.getValue()),
                            ZoneId.systemDefault()
                    );
                    ZonedDateTime endDateTime = ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(start.getValue()),
                            ZoneId.systemDefault()
                    );

                    TaskEntity task = new TaskEntity();
                    task.setDescription(description);
                    //task.setName(name); ?? name в гугл календаре эт кто
                    task.setStart(startDateTime);
                    task.setEnd(endDateTime);
                    task.setStatus(false);

                    taskRepository.save(task);
                }
            }
        } catch (IOException e) {

        }

        return new SimpleResponse();
    }


    @RequestMapping(value = "/to-google")
    public @ResponseBody SimpleResponse setEvents(
            @RequestParam(value = "projectId") long projectId
    ) {
        List<TaskEntity> tasks = new ArrayList<>();

        for (TaskEntity t : taskRepository.findAll()) {
            if (t.getProject().getId() == projectId) {
                // ids.add(t.getId());
                tasks.add(t);
            }
        }

        try {
            for (var t : tasks) {
                Event event = new Event()
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
                System.out.printf("Event created: %s\n", event.getHtmlLink());
            }
        } catch (IOException e) {

        }
        return new SimpleResponse();
    }
}