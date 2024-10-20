package com.joaogoncalves.feedback.controller;


import com.joaogoncalves.feedback.model.FeedbackCreate;
import com.joaogoncalves.feedback.model.FeedbackRead;
import com.joaogoncalves.feedback.model.FeedbackUpdate;
import com.joaogoncalves.feedback.model.UserLogin;
import com.joaogoncalves.feedback.service.AuthenticationService;
import com.joaogoncalves.feedback.testcontainers.EnableTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class FeedbackControllerIT {

    @Autowired
    private AuthenticationService authenticationService;

    @LocalServerPort
    private Integer port;

    private final String[] jwtTokens = new String[2];

    private final String invalidJWT = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjdXN0b21lci1mZWVkYmFjay1zZXJ2aWNlIiwic3ViIjoiMTIzNDUiLCJpYXQiOjE3MjgwNzM1NzIsImV4cCI6MTcyODA3NDQ3Mn0.FzJ-wAs_fccjc7bM71Vhu1ItbMd6KbMchfhWIyHGQGiEJI4Hj-VcpSJIAQYU32nN88b_CZvzcYFCSgyFg7qOAA";

    private String createdFeedbackId;

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        jwtTokens[0] = generateJwtToken(new UserLogin("test", "testtest"));
        jwtTokens[1] = generateJwtToken(new UserLogin("test2", "testtest"));
    }

    private String generateJwtToken(final UserLogin userLogin) {
        return authenticationService.login(userLogin).getAccessToken();
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
                ),
                Arguments.of(
                        new FeedbackCreate(
                                4,
                                "",
                                "test",
                                "MacBook Air",
                                "Online Trade LLC"
                        ),
                        jwtTokens[0],
                        HttpStatus.BAD_REQUEST.value(),
                        "feedback: must not be blank"
                ),
                Arguments.of(
                        new FeedbackCreate(
                                4,
                                "good but expensive",
                                "",
                                "MacBook Air",
                                "Online Trade LLC"
                        ),
                        jwtTokens[0],
                        HttpStatus.BAD_REQUEST.value(),
                        "user: must not be blank; user: size must be between 1 and 30"
                ),
                Arguments.of(
                        new FeedbackCreate(
                                4,
                                "good but expensive",
                                TestUtils.generateBigString.get(),
                                "MacBook Air",
                                "Online Trade LLC"
                        ),
                        jwtTokens[0],
                        HttpStatus.BAD_REQUEST.value(),
                        "user: size must be between 1 and 30"
                ),
                Arguments.of(
                        new FeedbackCreate(
                                4,
                                "good but expensive",
                                "test",
                                "MacBook Air",
                                ""
                        ),
                        jwtTokens[0],
                        HttpStatus.BAD_REQUEST.value(),
                        "vendor: must not be blank; vendor: size must be between 1 and 30"
                ),
                Arguments.of(
                        new FeedbackCreate(
                                4,
                                "good but expensive",
                                "test",
                                "MacBook Air",
                                TestUtils.generateBigString.get()
                        ),
                        jwtTokens[0],
                        HttpStatus.BAD_REQUEST.value(),
                        "vendor: size must be between 1 and 30"
                )
        );
    }

    Stream<Arguments> provideFeedbackReadsWithError() {
        return Stream.of(
                Arguments.of(
                        "4b437406-abf0-459a-a22f-5c65f1cf102a",
                        invalidJWT,
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid access token"
                ),
                Arguments.of(
                        "dec7fcec-5b86-43ae-902f-83180dd37cf1",
                        jwtTokens[0],
                        HttpStatus.NOT_FOUND.value(),
                        "Feedback not found! [Id: dec7fcec-5b86-43ae-902f-83180dd37cf1]"
                )
        );
    }

    Stream<Arguments> provideFeedbackUpdatesWithError() {
        return Stream.of(
                Arguments.of(
                        "4b437406-abf0-459a-a22f-5c65f1cf102a",
                        invalidJWT,
                        new FeedbackUpdate(
                            5,
                            "very good",
                            "MacBook Pro",
                            "Fnac"
                        ),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid access token"
                ),
                Arguments.of(
                        "dec7fcec-5b86-43ae-902f-83180dd37cf1",
                        jwtTokens[0],
                        new FeedbackUpdate(
                                5,
                                "very good",
                                "MacBook Pro",
                                "Fnac"
                        ),
                        HttpStatus.NOT_FOUND.value(),
                        "Feedback not found! [Id: dec7fcec-5b86-43ae-902f-83180dd37cf1]"
                ),
                Arguments.of(
                        createdFeedbackId,
                        jwtTokens[1],
                        new FeedbackUpdate(
                                5,
                                "very good",
                                "MacBook Pro",
                                "Fnac"
                        ),
                        HttpStatus.FORBIDDEN.value(),
                        String.format(
                                "User test2 is not the author of the feedback [Id: %s]",
                                createdFeedbackId
                        )
                ),
                Arguments.of(
                        createdFeedbackId,
                        jwtTokens[0],
                        new FeedbackUpdate(
                                5,
                                "very good",
                                TestUtils.generateBigString.get(),
                                "Fnac"
                        ),
                        HttpStatus.BAD_REQUEST.value(),
                        "product: size must be between 0 and 30"
                ),
                Arguments.of(
                        createdFeedbackId,
                        jwtTokens[0],
                        new FeedbackUpdate(
                                5,
                                "very good",
                                "MacBook Pro",
                                TestUtils.generateBigString.get()
                        ),
                        HttpStatus.BAD_REQUEST.value(),
                        "vendor: size must be between 0 and 30"
                )
        );
    }

    Stream<Arguments> provideFeedbackDeletesWithError() {
        return Stream.of(
                Arguments.of(
                        "4b437406-abf0-459a-a22f-5c65f1cf102a",
                        invalidJWT,
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid access token"
                ),
                Arguments.of(
                        "dec7fcec-5b86-43ae-902f-83180dd37cf1",
                        jwtTokens[0],
                        HttpStatus.NOT_FOUND.value(),
                        "Feedback not found! [Id: dec7fcec-5b86-43ae-902f-83180dd37cf1]"
                ),
                Arguments.of(
                        createdFeedbackId,
                        jwtTokens[1],
                        HttpStatus.FORBIDDEN.value(),
                        String.format(
                                "User test2 is not the author of the feedback [Id: %s]",
                                createdFeedbackId
                        )
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
        final Response response = given()
            .auth()
            .oauth2(jwtTokens[0])
            .contentType(ContentType.JSON)
            .body(feedbackCreate)
            .when()
            .post("/api/feedbacks/new")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response();
        final String location = response.getHeader("Location");
        createdFeedbackId = location.substring(location.lastIndexOf("/") + 1);
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

    @Test
    @Order(3)
    public void testFeedbackRead() {
        final FeedbackRead expectedFeedback = new FeedbackRead(
                createdFeedbackId,
                4,
                "good but expensive",
                "test",
                "MacBook Air",
                "Online Trade LLC"
        );
        final FeedbackRead actualFeedback = given()
                .auth()
                .oauth2(jwtTokens[0])
                .contentType(ContentType.JSON)
                .pathParam("id", createdFeedbackId)
                .when()
                .get("/api/feedbacks/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(FeedbackRead.class);
        assertThat(actualFeedback).isEqualTo(expectedFeedback);
    }

    @ParameterizedTest
    @Order(4)
    @MethodSource("provideFeedbackReadsWithError")
    public void testFeedbackReadWithError(final String id,
                                          final String token,
                                          final int expectedStatusCode,
                                          final String expectedExceptionMessage) {
        given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .get("/api/feedbacks/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("message",equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(5)
    public void testFeedbackUpdate() {
        final FeedbackUpdate feedbackUpdate = new FeedbackUpdate(
                5,
                "very good",
                "",
                null
        );
        final FeedbackRead expectedFeedback = new FeedbackRead(
                createdFeedbackId,
                5,
                "very good",
                "test",
                "MacBook Air",
                "Online Trade LLC"
        );
        final FeedbackRead actualFeedback = given()
                .auth()
                .oauth2(jwtTokens[0])
                .contentType(ContentType.JSON)
                .pathParam("id", createdFeedbackId)
                .body(feedbackUpdate)
                .when()
                .put("/api/feedbacks/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(FeedbackRead.class);
        assertThat(actualFeedback).isEqualTo(expectedFeedback);
    }


    @ParameterizedTest
    @Order(6)
    @MethodSource("provideFeedbackUpdatesWithError")
    public void testFeedbackUpdateWithError(final String id,
                                            final String token,
                                            final FeedbackUpdate feedbackUpdate,
                                            final int expectedStatusCode,
                                            final String expectedExceptionMessage) {
        given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(feedbackUpdate)
                .when()
                .put("/api/feedbacks/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("message",equalTo(expectedExceptionMessage));
    }

    @ParameterizedTest
    @Order(7)
    @MethodSource("provideFeedbackDeletesWithError")
    public void testFeedbackDeleteWithError(final String id,
                                            final String token,
                                            final int expectedStatusCode,
                                            final String expectedExceptionMessage) {
        given()
                .auth()
                .oauth2(token)
                .pathParam("id", id)
                .when()
                .delete("/api/feedbacks/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("message",equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(8)
    public void testFeedbackDelete() {
        given()
                .auth()
                .oauth2(jwtTokens[0])
                .pathParam("id", createdFeedbackId)
                .when()
                .delete("/api/feedbacks/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
