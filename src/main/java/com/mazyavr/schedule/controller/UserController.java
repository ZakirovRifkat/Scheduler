package com.mazyavr.schedule.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mazyavr.schedule.dto.UserDto;
import com.mazyavr.schedule.entity.UserEntity;
import com.mazyavr.schedule.service.UserService;
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
  
  @Autowired
  private UserService userService;
  
  private String token;
  
  @GetMapping(value = "/get-cookie")
  public ResponseEntity<?> readCookie(@CookieValue(value = "data") String data) {
    return ResponseEntity.ok().body(data);
  }
  
  @PostMapping(path = "/login")
  public @ResponseBody RedirectView login(
    @RequestParam(value = "code") String code,
    @RequestParam(value = "state") String redirectUri
  ) {
    try {
      GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
        new NetHttpTransport(), new GsonFactory(),
        "530835646351-odkl0qt3l07n6s7httrpsd6rd37ka5l9.apps.googleusercontent.com", "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",
        code, "http://localhost:8080/users/login")
        .execute();
      
      this.token = response.getAccessToken();
    } catch (IOException e) {
    }
  
    return new RedirectView(redirectUri);
  }
  
  @GetMapping(path = "/logout")
  public ResponseEntity<?>  logout(HttpServletResponse response) throws IOException {
    
    Cookie cookie = new Cookie("status", "expired");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    response.setContentType("text/plain");
    
    return ResponseEntity.ok().body(HttpStatus.OK);//получилось как бы два раза статус ответа установили, выбирайте какой вариант лучше
  }
}
