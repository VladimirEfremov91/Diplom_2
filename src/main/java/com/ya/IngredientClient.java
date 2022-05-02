package com.ya;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class IngredientClient extends StellarburgersRestClient {
    private static final String INGREDIENTS_PATH = "api/ingredients";

    @Step("Запрос ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS_PATH)
                .then().log().all();
    }
}