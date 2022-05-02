package com.ya;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderClient extends StellarburgersRestClient {
    private static final String ORDER_PATH = "api/orders/";

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }
    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createOrderWithAuth(Order order, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }
    @Step("Получение заказа конкретного пользователя с авторизацией")
    public ValidatableResponse getOrdersOfUserWithAuth(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }
    @Step("Получение заказа конкретного пользователя без авторизацией")
    public ValidatableResponse getOrdersOfUserWithoutAuth() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }
}
