package com.mazyavr.schedule.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @PostMapping
    public ResponseEntity<UserDto> register(@RequestBody User user) {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
