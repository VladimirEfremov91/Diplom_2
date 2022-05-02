import com.ya.*;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@Epic("Работа с пользователями")
@Feature("Создание пользователя")
public class CreateUserTest {
    UserClient userClient;
    User user;
    UserTokens userTokens;

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
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
    @DisplayName("Создание пользователя")
    @Description("123")
    public void userCanBeCreated() {
        userClient = new UserClient();
        ValidatableResponse createUser = userClient.createUser(user);
        userTokens = new UserTokens(createUser.extract().path("refreshToken"), createUser.extract().path("accessToken"));
        assertEquals(createUser.extract().statusCode(), 200);
        assertEquals(createUser.extract().path("user.email"), user.getEmail());
        assertEquals(createUser.extract().path("user.name"), user.getName());
    }
    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void canNotCreateRegisteredUser() {
        userClient = new UserClient();
        ValidatableResponse createFirstUser = userClient.createUser(user);
        userTokens = new UserTokens(createFirstUser.extract().path("refreshToken"), createFirstUser.extract().path("accessToken"));
        ValidatableResponse createSecondUser = userClient.createUser(user);
        assertEquals(createSecondUser.extract().statusCode(), 403);
        assertEquals(createSecondUser.extract().path("message"), "User already exists");
    }
    @Test
    @DisplayName("Создание пользователя без email")
    public void canNotCreateUserWithoutEmail() {
        User userWithoutEmail = User.builder()
                .password(user.getPassword())
                .name(user.getName())
                .build();
        userClient = new UserClient();
        ValidatableResponse createUser = userClient.createUser(userWithoutEmail);
        assertEquals(createUser.extract().statusCode(), 403);
        assertEquals(createUser.extract().path("message"), "Email, password and name are required fields");
    }
    @Test
    @DisplayName("Создание пользователя без пароля")
    public void canNotCreateUserWithoutPassword() {
        User userWithoutPassword = User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
        userClient = new UserClient();
        ValidatableResponse createUser = userClient.createUser(userWithoutPassword);
        assertEquals(createUser.extract().statusCode(), 403);
        assertEquals(createUser.extract().path("message"), "Email, password and name are required fields");
    }
    @Test
    @DisplayName("Создание пользователя без имени")
    public void canNotCreateUserWithoutName() {
        User userWithoutName = User.builder()
                .email(user.getEmail())
                .name(user.getPassword())
                .build();
        userClient = new UserClient();
        ValidatableResponse createUser = userClient.createUser(userWithoutName);
        assertEquals(createUser.extract().statusCode(), 403);
        assertEquals(createUser.extract().path("message"), "Email, password and name are required fields");
    }
}