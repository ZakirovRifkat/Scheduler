package com.mazyavr.schedule.controller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;
import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.entity.ProjectEntity;
import com.mazyavr.schedule.entity.TaskEntity;
import com.mazyavr.schedule.repository.ProjectRepository;
import com.mazyavr.schedule.repository.TaskRepository;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.Calendar;
import com.mazyavr.schedule.entity.ProjectEntity;
import com.mazyavr.schedule.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

class HomeController {
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        @Autowired
        TaskRepository taskRepository;

        @RequestMapping(value = "/from-google")
        public ResponseEntity<String> getEvents(
                @RequestParam(value = "token") String token,
                @RequestParam(value = "sdate") String sdate,
                @RequestParam(value = "edate") String edate,
                @RequestParam(value = "q") String q
        ) {
                // Представляем, что авторизация работает и что объект service создан
                // Дальше здесь нужно получить список событий из гугла и сохранить

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




                        var projectO = projectRepository.findById(projectId);

                        if (projectO.isEmpty()) {
                                throw new IllegalArgumentException("No such project");
                        }

                        ProjectEntity project = projectO.get();
                        task.setProject(project);

                        taskRepository.save(task);
                } catch (IOException e) {

                }

                return new SimpleResponse();
        }


        @RequestMapping(value = "/to-google")
        public ResponseEntity<String> getEvents(
                @RequestParam(value = "token") String token,
                @RequestParam(value = "sdate") String sdate,
                @RequestParam(value = "edate") String edate,
                @RequestParam(value = "projectId") long projectId,
                @RequestParam(value = "q") String q
        ) {
                // Представляем, что авторизация работает и что объект service создан
                // Дальше здесь нужно наш список событий из базы данных загрузить в гугл
                List<Long> ids = new ArrayList<>();
                List<TaskEntity> tasks = new ArrayList();

                for (TaskEntity t : taskRepository.findAll()) {
                        if (t.getProject().getId() == projectId) {
                                // ids.add(t.getId());
                                tasks.add(t);
                        }
                }

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
        }
}