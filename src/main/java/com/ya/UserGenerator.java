package com.ya;
import com.github.javafaker.Faker;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class UserGenerator {

    static Faker faker = new Faker();

    @Step("Генерация случайных данных пользователя")
    public static User getRandom() {
        final String userEmail = faker.internet().emailAddress();
        final String userPassword = faker.internet().password(6,8, true);
        final String userName = faker.name().username();
        Allure.addAttachment("Email: ", userEmail);
        Allure.addAttachment("Пароль: ", userPassword);
        Allure.addAttachment("Имя: ", userName);
        return new User(userEmail, userPassword, userName);
    }
}