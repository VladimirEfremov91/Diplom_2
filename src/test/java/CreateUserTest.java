import com.ya.*;
import io.qameta.allure.Description;
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
@Feature("Создание пользователя")
public class CreateUserTest {
    static UserClient userClient;
    User user;
    UserTokens userTokens;
    @BeforeClass
    public static void getReady() {
        userClient = new UserClient();
    }

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
    }

    @After
    //Здесь и далее переписанный tearDown. Отказался от исключения, теперь проверяю сам токен.
    public void tearDown() {
        if (userTokens.getAccessToken() != null) {
            userClient.deleteUser(userTokens.getAccessToken());
        } else {
            System.out.println("Пользователя невозможно удалить, так как он не был создан.");
        }
    }

    @Test
    @DisplayName("Создание пользователя")
    public void userCanBeCreated() {
        ValidatableResponse createUser = userClient.createUser(user);
        userTokens = userClient.tokensExtractor(createUser);
        assertEquals(createUser.extract().statusCode(), 200);
        assertEquals(createUser.extract().path("user.email"), user.getEmail());
        assertEquals(createUser.extract().path("user.name"), user.getName());
    }
    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void canNotCreateRegisteredUser() {
        ValidatableResponse createFirstUser = userClient.createUser(user);
        userTokens = userClient.tokensExtractor(createFirstUser);
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
        ValidatableResponse createUser = userClient.createUser(userWithoutEmail);
        userTokens = userClient.tokensExtractor(createUser);
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
        ValidatableResponse createUser = userClient.createUser(userWithoutPassword);
        userTokens = userClient.tokensExtractor(createUser);
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
        ValidatableResponse createUser = userClient.createUser(userWithoutName);
        userTokens = userClient.tokensExtractor(createUser);
        assertEquals(createUser.extract().statusCode(), 403);
        assertEquals(createUser.extract().path("message"), "Email, password and name are required fields");
    }
}