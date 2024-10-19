package com.joaogoncalves.feedback.controller;


import com.joaogoncalves.feedback.model.FeedbackCreate;
import com.joaogoncalves.feedback.model.UserLogin;
import com.joaogoncalves.feedback.service.AuthenticationService;
import com.joaogoncalves.feedback.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class FeedbackControllerIT {

    @Autowired
    private AuthenticationService authenticationService;

    @LocalServerPort
    private Integer port;

    private String jwtToken;

    private String invalidJWT = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjdXN0b21lci1mZWVkYmFjay1zZXJ2aWNlIiwic3ViIjoiMTIzNDUiLCJpYXQiOjE3MjgwNzM1NzIsImV4cCI6MTcyODA3NDQ3Mn0.FzJ-wAs_fccjc7bM71Vhu1ItbMd6KbMchfhWIyHGQGiEJI4Hj-VcpSJIAQYU32nN88b_CZvzcYFCSgyFg7qOAA";

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        jwtToken = generateJwtToken();
    }

    private String generateJwtToken() {
        return authenticationService.login(
                new UserLogin("test", "testtest")
        ).getAccessToken();
    }

    Stream<Arguments> provideFeedbackCreatesWithError() {
        return Stream.of(
                Arguments.of(
                        new FeedbackCreate(
                                4,
                                "good but expensive",
                                "test",
                                "MacBook Air",
                                "Online Trade LLC"
                        ),
                        invalidJWT,
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid access token"
                )
        );
    }

    @Test
    @Order(1)
    public void testFeedbackCreate() {
        final FeedbackCreate feedbackCreate = new FeedbackCreate(
                4,
                "good but expensive",
                "test",
                "MacBook Air",
                "Online Trade LLC"
        );
        given()
            .auth()
            .oauth2(jwtToken)
            .contentType(ContentType.JSON)
            .body(feedbackCreate)
            .when()
            .post("/api/feedbacks/new")
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @Order(2)
    @MethodSource("provideFeedbackCreatesWithError")
    public void testFeedbackCreateWithError(final FeedbackCreate feedbackCreate,
                                          final String authToken,
                                          final int expectedStatusCode,
                                          final String expectedExceptionMessage) {
        given()
            .auth()
            .oauth2(authToken)
            .contentType(ContentType.JSON)
            .body(feedbackCreate)
            .when()
            .post("/api/feedbacks/new")
            .then()
            .statusCode(expectedStatusCode)
            .body("message", equalTo(expectedExceptionMessage));
    }
}
