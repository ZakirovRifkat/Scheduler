package com.mazyavr.schedule.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.mazyavr.schedule.Config;
import com.mazyavr.schedule.dto.SimpleResponse;
import com.mazyavr.schedule.entity.UserEntity;
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
import java.util.Collections;
import java.util.Optional;


@Controller
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    final String USER_ID_COOKIE = "userId";
    String REDIRECT_URI = "http://localhost:8080/user/login";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Config config;

    @GetMapping(path = "/login")
    public @ResponseBody RedirectView login(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "state") String redirectUri,
            HttpServletResponse servletResponse
    ) throws IOException, GeneralSecurityException {
        var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                HTTP_TRANSPORT, JSON_FACTORY, config.getGoogleClientId(),
                config.getGoogleClientSecret(), code, REDIRECT_URI)
                .execute();

        var idToken = response.getIdToken();
        var c = response.parseIdToken();
        var verifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
                .setAudience(Collections.singletonList(config.getGoogleClientId())).build();

        var verifiedToken = verifier.verify(idToken);

        if (verifiedToken == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user ID provided");
        }

        var googleId = verifiedToken.getPayload().getSubject();

        UserEntity user = null;

        for (UserEntity t : userRepository.findAll()) {
            if (googleId.equals(t.getGoogleId())) {
                user = t;
            }
        }

        if (user == null) {
            user = new UserEntity();
            user.setRefreshToken(response.getRefreshToken());
            user.setGoogleId(googleId);
            userRepository.save(user);
        }

        setUserIdCookie(user.getId(), servletResponse);

        return new RedirectView(redirectUri);
    }

    private void setUserIdCookie(long userId, HttpServletResponse servletResponse) {

        Cookie cookie = new Cookie("userId", Long.toString(userId));
        cookie.setPath("/");
        cookie.setMaxAge(604800); // 7 дней
        servletResponse.addCookie(cookie);
    }

    @PostMapping(path = "/logout")
    public @ResponseBody SimpleResponse logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(USER_ID_COOKIE, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return new SimpleResponse();
    }

    @GetMapping(value = "/me")
    public @ResponseBody UserResponse getMe(@CookieValue(value = USER_ID_COOKIE, required = false) Long userId) {
        if (userId == null) {
            return new UserResponse(Optional.empty());
        }

        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return new UserResponse(Optional.empty());
        }

        return new UserResponse(Optional.of(user.get().getId()));
    }

    private record UserResponse(Optional<Long> userId) {

    }
}
