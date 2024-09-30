package com.joaogoncalves.feedback.controller;

import com.joaogoncalves.feedback.model.TokenLogout;
import com.joaogoncalves.feedback.model.TokenRefresh;
import com.joaogoncalves.feedback.model.UserCreate;
import com.joaogoncalves.feedback.model.UserLogin;
import com.joaogoncalves.feedback.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/auth")
@Validated
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(path="/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@Valid @RequestBody final UserLogin userLogin) {
        log.info("User [username: {}] is logging in", userLogin.getUsername());
        return ResponseEntity.ok(
                authenticationService.login(userLogin)
        );
    }

    @PostMapping(path = "/signup", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> signup(@Valid @RequestBody final UserCreate userCreate) {
        log.info("User [username: {}] is signing up", userCreate.getUsername());
        return new ResponseEntity<>(authenticationService.signup(userCreate), HttpStatus.CREATED);
    }

    @PostMapping(path ="/logout", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> logout(@Valid @RequestBody TokenLogout tokenLogout) {
        log.info("User [ID: {}] is logging out", tokenLogout.getUserId());
        authenticationService.logout(tokenLogout);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/refresh-token", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefresh tokenRefresh) {
        log.info("User [ID: {}] is refreshing token", tokenRefresh.getUserId());
        return ResponseEntity.ok(
                authenticationService.refreshToken(tokenRefresh)
        );
    }
}
