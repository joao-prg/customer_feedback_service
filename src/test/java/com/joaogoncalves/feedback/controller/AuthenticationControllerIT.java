package com.joaogoncalves.feedback.controller;

import com.joaogoncalves.feedback.model.TokenLogout;
import com.joaogoncalves.feedback.model.TokenRefresh;
import com.joaogoncalves.feedback.model.UserCreate;
import com.joaogoncalves.feedback.model.UserLogin;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class AuthenticationControllerIT {

    @LocalServerPort
    private Integer port;

    private static String invalidJWT = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjdXN0b21lci1mZWVkYmFjay1zZXJ2aWNlIiwic3ViIjoiMTIzNDUiLCJpYXQiOjE3MjgwNzM1NzIsImV4cCI6MTcyODA3NDQ3Mn0.FzJ-wAs_fccjc7bM71Vhu1ItbMd6KbMchfhWIyHGQGiEJI4Hj-VcpSJIAQYU32nN88b_CZvzcYFCSgyFg7qOAA";
    private String accessToken;
    private String refreshToken;

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    static Stream<Arguments> provideLoginsWithError() {
        return Stream.of(
                Arguments.of(
                        new UserLogin("testwrong", "testtest"),
                        "Bad credentials",
                        HttpStatus.UNAUTHORIZED
                ),
                Arguments.of(
                        new UserLogin("", "testtest"),
                        "username: must not be blank; username: size must be between 1 and 30",
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        new UserLogin("test", "testtes"),
                        "password: size must be between 8 and 30",
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        new UserLogin("test",  ""),
                        "password: must not be blank; password: size must be between 8 and 30",
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        new UserLogin("test",  TestUtils.generateBigString.get()),
                        "password: size must be between 8 and 30",
                        HttpStatus.BAD_REQUEST
                )
        );
    }

    static Stream<Arguments> provideSignupsWithError() {
        return Stream.of(
                Arguments.of(
                        new UserCreate("test", "test@test.com", "testtest"),
                        "User already exists! [username: test]"
                ),
                Arguments.of(
                        new UserCreate("","test@test.com", "testtest"),
                        "username: must not be blank; username: size must be between 1 and 30"
                ),
                Arguments.of(
                        new UserCreate("test", "testtest.com", "testtest"),
                        "email: Invalid email format"
                ),
                Arguments.of(
                        new UserCreate("test","", "testtest"),
                        "email: must not be blank"
                ),
                Arguments.of(
                        new UserCreate("test","test@test.com", "testtes"),
                        "password: size must be between 8 and 30"
                ),
                Arguments.of(
                        new UserCreate("test", "test@test.com", ""),
                        "password: must not be blank; password: size must be between 8 and 30"
                )
        );
    }

    static Stream<Arguments> provideLogoutsWithError() {
        return Stream.of(
                Arguments.of(
                        new TokenLogout("test", invalidJWT, invalidJWT),
                        "Invalid refresh token",
                        HttpStatus.UNAUTHORIZED
                ),
                Arguments.of(
                        new TokenLogout("", invalidJWT, invalidJWT),
                        "username: Username cannot be blank",
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        new TokenLogout("test", "", invalidJWT),
                        "accessToken: Access token cannot be blank",
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        new TokenLogout("test", invalidJWT, ""),
                        "refreshToken: Refresh token cannot be blank",
                        HttpStatus.BAD_REQUEST
                )
        );
    }

    static Stream<Arguments> provideRefreshTokensWithError() {
        return Stream.of(
                Arguments.of(
                        new TokenRefresh("test", invalidJWT, invalidJWT),
                        "Invalid refresh token",
                        HttpStatus.UNAUTHORIZED
                ),
                Arguments.of(
                        new TokenRefresh("", invalidJWT, invalidJWT),
                        "username: Username cannot be blank",
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        new TokenRefresh("test", "", invalidJWT),
                        "accessToken: Access token cannot be blank",
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        new TokenRefresh("test", invalidJWT, ""),
                        "refreshToken: Refresh token cannot be blank",
                        HttpStatus.BAD_REQUEST
                )
        );
    }

    @Test
    @Order(1)
    public void testLogin() {
        final UserLogin userLogin = new UserLogin("test", "testtest");
        final Response response = (Response) given()
                .contentType(ContentType.JSON)
                .body(userLogin)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();
        refreshToken = response.path("refreshToken");
        accessToken = response.path("accessToken");
    }

    @ParameterizedTest
    @MethodSource("provideLoginsWithError")
    @Order(2)
    public void testLoginWithError(final UserLogin userLogin,
                                   final String expectedExceptionMessage,
                                   final HttpStatus httpStatus) {
        given()
                .contentType(ContentType.JSON)
                .body(userLogin)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(httpStatus.value())
                .body("message", equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(3)
    public void testSignup() {
        final UserCreate userCreate = new UserCreate("random", "random@random.com", "random12");
        given()
                .contentType(ContentType.JSON)
                .body(userCreate)
                .when()
                .post("/api/auth/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @MethodSource("provideSignupsWithError")
    @Order(4)
    public void testSignupWithError(final UserCreate userCreate, final String expectedExceptionMessage) {
        given()
                .contentType(ContentType.JSON)
                .body(userCreate)
                .when()
                .post("/api/auth/signup")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(5)
    public void testRefreshToken() {
        final TokenRefresh tokenRefresh = new TokenRefresh("random", accessToken, refreshToken);
        given()
                .contentType(ContentType.JSON)
                .body(tokenRefresh)
                .when()
                .post("/api/auth/refresh-token")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @ParameterizedTest
    @MethodSource("provideRefreshTokensWithError")
    @Order(6)
    public void testRefreshTokenWithError(final TokenRefresh tokenRefresh,
                                          final String expectedExceptionMessage,
                                          final HttpStatus httpStatus) {
        given()
                .contentType(ContentType.JSON)
                .body(tokenRefresh)
                .when()
                .post("/api/auth/refresh-token")
                .then()
                .statusCode(httpStatus.value())
                .body("message", equalTo(expectedExceptionMessage));
    }

    @Test
    @Order(7)
    public void testLogout() {
        final TokenLogout tokenLogout = new TokenLogout("test", accessToken, refreshToken);
        given()
                .contentType(ContentType.JSON)
                .body(tokenLogout)
                .when()
                .post("/api/auth/logout")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @ParameterizedTest
    @MethodSource("provideLogoutsWithError")
    @Order(8)
    public void testLogoutWithError(final TokenLogout tokenLogout,
                                    final String expectedExceptionMessage,
                                    final HttpStatus httpStatus) {
        given()
                .contentType(ContentType.JSON)
                .body(tokenLogout)
                .when()
                .post("/api/auth/logout")
                .then()
                .statusCode(httpStatus.value())
                .body("message", equalTo(expectedExceptionMessage));
    }
}