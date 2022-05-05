import com.ya.*;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@Epic("Работа с пользователями")
@Feature("Изменение данных пользователя")
public class UpdateUserTest {
    static UserClient userClient;
    User user;
    UserCredentials userCredentials;
    UserTokens userTokens;

    @BeforeClass
    public static void getReady() {
        userClient = new UserClient();
    }

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        userClient.createUser(user);
        ValidatableResponse loginTestUser = userClient.loginUser(userCredentials);
        userTokens = userClient.tokensExtractor(loginTestUser);
    }

    @After
    public void tearDown() {
        if (userTokens.getAccessToken() != null) {
            userClient.deleteUser(userTokens.getAccessToken());
        } else {
            System.out.println("Пользователя невозможно удалить, так как он не был создан.");
        }
    }

    @Test
    @DisplayName("Изменение пароля без авторизации")
    public void updateUserPasswordWithoutAuth() {
        User userWithNewPassword = User.builder()
                .password(user.getPassword() + "a")
                .name(user.getName())
                .email(user.getEmail())
                .build();
        ValidatableResponse updateUser = userClient.updateUserWithoutToken(userWithNewPassword);
        assertEquals(401, updateUser.extract().statusCode());
        assertEquals("You should be authorised", updateUser.extract().path("message"));
    }
    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    public void updateUserNameWithoutAuth() {
        User userWithNewPassword = User.builder()
                .password(user.getPassword())
                .name(user.getName() + "a")
                .email(user.getEmail())
                .build();
        ValidatableResponse updateUser = userClient.updateUserWithoutToken(userWithNewPassword);
        assertEquals(401, updateUser.extract().statusCode());
        assertEquals("You should be authorised", updateUser.extract().path("message"));
    }
    @Test
    @DisplayName("Изменение email пользователя без авторизации")
    public void updateUserEmailWithoutAuth() {
        User userWithNewPassword = User.builder()
                .password(user.getPassword())
                .name(user.getName())
                .email(user.getEmail() + "a")
                .build();
        ValidatableResponse updateUser = userClient.updateUserWithoutToken(userWithNewPassword);
        assertEquals(401, updateUser.extract().statusCode());
        assertEquals("You should be authorised", updateUser.extract().path("message"));
    }
    @Test
    @DisplayName("Изменение пароля с авторизацией")
    public void updateUserPasswordWithAuth() {
        User userWithNewPassword = User.builder()
                .password(user.getPassword() + "a")
                .name(user.getName())
                .email(user.getEmail())
                .build();
        ValidatableResponse updateUser = userClient.updateUserWithToken(userWithNewPassword, userTokens.getAccessToken());
        assertEquals(200, updateUser.extract().statusCode());
        assertEquals(userWithNewPassword.getEmail(), updateUser.extract().path("user.email"));
        assertEquals(userWithNewPassword.getName(), updateUser.extract().path("user.name"));
    }
    @Test
    @DisplayName("Изменение имени пользователя c авторизацией")
    public void updateUserNameWithAuth() {
        User userWithNewName = User.builder()
                .password(user.getPassword())
                .name(user.getName() + "a")
                .email(user.getEmail())
                .build();
        ValidatableResponse updateUser = userClient.updateUserWithToken(userWithNewName, userTokens.getAccessToken());
        assertEquals(200, updateUser.extract().statusCode());
        assertEquals(userWithNewName.getEmail(), updateUser.extract().path("user.email"));
        assertEquals(userWithNewName.getName(), updateUser.extract().path("user.name"));
    }
    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void updateUserEmailWithAuth() {
        User userWithNewEmail = User.builder()
                .password(user.getPassword())
                .name(user.getName())
                .email(user.getEmail() + "a")
                .build();
        ValidatableResponse updateUser = userClient.updateUserWithToken(userWithNewEmail, userTokens.getAccessToken());
        assertEquals(200, updateUser.extract().statusCode());
        assertEquals(userWithNewEmail.getEmail(), updateUser.extract().path("user.email"));
        assertEquals(userWithNewEmail.getName(), updateUser.extract().path("user.name"));
    }
}