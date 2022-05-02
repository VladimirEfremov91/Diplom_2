package com.ya;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserClient extends StellarburgersRestClient {
    private static final String USER_PATH = "api/auth";

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(USER_PATH + "/register")
                .then().log().all();
    }
    @Step("Создание пользователя без обязательного поля")
    public String createFailedUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(USER_PATH + "/register")
                .then().log().all()
                .assertThat()
                .statusCode(403)
                .extract()
                .path("message");
    }
    @Step("Логин пользователя")
    public ValidatableResponse loginUser(UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .when()
                .post(USER_PATH + "/login")
                .then().log().all();
    }
    @Step("Выход из системы")
    public ValidatableResponse logoutUser(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body("{\"token\": \"" + refreshToken + "\"}")
                .when()
                .post(USER_PATH + "/logout")
                .then().log().all();
    }
    @Step("Обновление данных пользователя без токена")
    public ValidatableResponse updateUserWithoutToken(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(USER_PATH + "/user")
                .then().log().all();
    }
    @Step("Обновление данных пользователя с токеном")
    public ValidatableResponse updateUserWithToken(User user, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(USER_PATH + "/user")
                .then().log().everything();
    }
    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .when()
                .delete(USER_PATH + "/user")
                .then().log().all();
    }
}