package com.mazyavr.schedule.controller;

@RequestMapping(value = "/events")
public ResponseEntity<String> getEvents(@AuthenticationPrincipal OAuth2User oAuth2User,
@RequestParam(value = "sdate") String sdate,
@RequestParam(value = "edate") String edate,
@RequestParam(value = "q") String q) {
        com.google.api.services.calendar.model.Events eventList;
        String message;
        try {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User)oAuth2User;
        String token = customOAuth2User.getToken();
        GoogleCredential credential = new GoogleCredential().setAccessToken(token);

final DateTime date1 = new DateTime(sdate + "T00:00:00");
final DateTime date2 = new DateTime(edate + "T23:59:59");

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME).build();
        Events events = service.events();
        eventList = events.list("primary").setTimeZone("Asia/Kolkata").setTimeMin(date1).setTimeMax(date2).setQ(q).execute();
        message = eventList.getItems().toString();
        System.out.println("My:" + eventList.getItems());
        } catch (Exception e) {

        message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
        + " Redirecting to google connection status page.";
        }

        return new ResponseEntity<>(message, HttpStatus.OK);
        }
