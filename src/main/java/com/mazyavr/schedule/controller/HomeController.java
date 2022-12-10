package com.mazyavr.schedule.controller;

class HomeController {
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        @RequestMapping(value = "/from-google")
        public ResponseEntity<String> getEvents(
                @RequestParam(value ="token") String token,
                @RequestParam(value = "sdate") String sdate,
                @RequestParam(value = "edate") String edate,
                @RequestParam(value = "q") String q
        ) {
                // Представляем, что авторизация работает и что объект service создан
                // Дальше здесь нужно получить список событий из гугла и сохранить
        }

        @RequestMapping(value = "/to-google")
        public ResponseEntity<String> getEvents(
                @RequestParam(value ="token") String token,
                @RequestParam(value = "sdate") String sdate,
                @RequestParam(value = "edate") String edate,
                @RequestParam(value = "q") String q
        ) {
                // Представляем, что авторизация работает и что объект service создан
                // Дальше здесь нужно наш список событий из базы данных загрузить в гугл
        }
}