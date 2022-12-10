package com.mazyavr.schedule.controller;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.api.client.json.gson.GsonFactory;

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

        @RequestMapping(value = "/from-google")
        public ResponseEntity<String> getEvents(
                @RequestParam(value = "token") String token,
                @RequestParam(value = "sdate") String sdate,
                @RequestParam(value = "edate") String edate,
                @RequestParam(value = "q") String q
        ) {
                // Представляем, что авторизация работает и что объект service создан
                // Дальше здесь нужно получить список событий из гугла и сохранить

                DateTime now = new DateTime(System.currentTimeMillis());
                Events events = service.events().list("primary")
                        .setMaxResults(1)
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
                                String description=event.getDescription();
                                DateTime end = event.getEnd().getDateTime();
                                if (end == null) {
                                        end = event.getEnd().getDate();
                                }
                                //System.out.printf("%s (%s)\n", event.getSummary(), start);

                        }
                }


                TaskEntity task = new TaskEntity();
                task.setDescription(description);
                //task.setName(name); ?? name в гугл календаре эт кто
                task.setStart(start);
                task.setEnd(end);
                task.setStatus(false);

                var projectO = projectRepository.findById(projectId);

                if (projectO.isEmpty()) {
                        throw new IllegalArgumentException("No such project");
                }

                ProjectEntity project = projectO.get();
                task.setProject(project);

                return taskRepository.save(task);
        }


        @RequestMapping(value = "/to-google")
        public ResponseEntity<String> getEvents(
                @RequestParam(value = "token") String token,
                @RequestParam(value = "sdate") String sdate,
                @RequestParam(value = "edate") String edate,
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

                Event event = new Event()
                        .setSummary("Google I/O 2015")
                        .setLocation("800 Howard St., San Francisco, CA 94103")
                        .setDescription(t.getDescription);

                DateTime startDateTime = new DateTime("2007-12-03T10:15:30+01:00");
                EventDateTime start = new EventDateTime()
                        .setDateTime(t.getStart)
                        .setTimeZone("Europe/Paris");
                event.setStart(start);

                DateTime endDateTime = new DateTime("2007-12-03T10:15:30+01:00");
                EventDateTime end = new EventDateTime()
                        .setDateTime(t.getEnd)
                        .setTimeZone("Europe/Paris");
                event.setEnd(end);


                // Надо или не?
                String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
                event.setRecurrence(Arrays.asList(recurrence));

                EventAttendee[] attendees = new EventAttendee[] {
                        new EventAttendee().setEmail("lpage@example.com"),
                        new EventAttendee().setEmail("sbrin@example.com"),
                };
                event.setAttendees(Arrays.asList(attendees));

                EventReminder[] reminderOverrides = new EventReminder[] {
                        new EventReminder().setMethod("email").setMinutes(24 * 60),
                        new EventReminder().setMethod("popup").setMinutes(10),
                };
                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(Arrays.asList(reminderOverrides));
                event.setReminders(reminders);
                //

                String calendarId = "primary";
                event = service.events().insert(calendarId, event).execute();
                System.out.printf("Event created: %s\n", event.getHtmlLink());

        }
}