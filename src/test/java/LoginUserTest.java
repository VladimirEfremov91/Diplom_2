import com.ya.*;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@Epic("Работа с пользователями")
@Feature("Логин пользователя")
public class LoginUserTest {
    UserClient userClient;
    User user;
    UserCredentials userCredentials;
    UserTokens userTokens;


    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandom();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        ValidatableResponse createTestUser = userClient.createUser(user);
        userTokens = new UserTokens(createTestUser.extract().path("refreshToken"), createTestUser.extract().path("accessToken"));
    }
    @After
    public void tearDown() {
        userClient = new UserClient();
        try {
            userClient.deleteUser(userTokens.getAccessToken());
        } catch (Exception exception) {
            System.out.println("Пользователя невозможно удалить, так как он не был создан.");
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void userCanBeLoggedIn() {
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        assertEquals(200, loginResponse.extract().statusCode());
        assertEquals(user.getEmail(), loginResponse.extract().path("user.email"));
        assertEquals(user.getName(), loginResponse.extract().path("user.name"));
        userTokens = new UserTokens(loginResponse.extract().path("refreshToken"), loginResponse.extract().path("accessToken"));

    }
    @Test
    @DisplayName("Логин c неправильным паролем")
    public void canNotLoginWithWrongPassword() {
        UserCredentials credentialsWithWrongPassword = UserCredentials.builder()
                .email(userCredentials.getEmail())
                .password(userCredentials.getPassword() + 1)
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentialsWithWrongPassword);
        assertEquals(401, loginResponse.extract().statusCode());
        assertEquals("email or password are incorrect", loginResponse.extract().path("message"));
    }
    @Test
    @DisplayName("Логин c неправильным email")
    public void canNotLoginWithWrongEmail() {
        UserCredentials credentialsWithWrongEmail = UserCredentials.builder()
                .email(userCredentials.getEmail() + 1)
                .password(userCredentials.getPassword())
                .build();
        ValidatableResponse loginResponse = userClient.loginUser(credentialsWithWrongEmail);
        assertEquals(401, loginResponse.extract().statusCode());
        assertEquals("email or password are incorrect", loginResponse.extract().path("message"));
    }
}
