import com.github.javafaker.Faker;
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
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Epic("Работа с заказами")
@Feature("Созадние заказа")
public class CreateOrderTest {
    static UserClient userClient;
    static OrderClient orderClient;
    static IngredientClient ingredientClient;
    User user;
    UserCredentials userCredentials;
    UserTokens userTokens;
    Order order;
    ArrayList ingredients;
    static Faker faker = new Faker();

    @BeforeClass
    public static void getReady() {
        userClient = new UserClient();
        ingredientClient = new IngredientClient();
        orderClient = new OrderClient();
    }

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        userClient.createUser(user);
        ValidatableResponse loginTestUser = userClient.loginUser(userCredentials);
        userTokens = userClient.tokensExtractor(loginTestUser);
        ValidatableResponse getIngredients = ingredientClient.getIngredients();
        ingredients = new ArrayList<>();
        ingredients.add(getIngredients.extract().path("data[0]._id"));
        ingredients.add(getIngredients.extract().path("data[1]._id"));
        order = new Order(ingredients);
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
    @DisplayName("Создание заказа без авторизации")
    @Description("Для ревьюера: В документации явно не указано, какой ответ и код ответа должна давать система при создании заказа без авторизации," +
            "поэтому проверка создана из предположения, что код ответа должен быть 401 и сообщение You should be authorised")
    public void orderCanNotBeCreatedWithoutAuth() {
        ValidatableResponse createOrder = orderClient.createOrderWithoutAuth(order);
        assertEquals(401, createOrder.extract().statusCode());
        assertEquals("You should be authorised", createOrder.extract().path("message"));
    }
    @Test
    @DisplayName("Создание заказа c авторизацией и ингредиентами")
    public void orderCanBeCreatedWithAuth() {
        ValidatableResponse createOrder = orderClient.createOrderWithAuth(order, userTokens.getAccessToken());
        assertEquals(200, createOrder.extract().statusCode());
        assertTrue(createOrder.extract().path("name").toString().contains("бургер"));
    }
    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void orderCanNotBeCreatedWithoutIngredients() {
        ArrayList nullIngredients = new ArrayList<>();
        Order nullOrder = new Order(nullIngredients);
        ValidatableResponse createOrder = orderClient.createOrderWithAuth(nullOrder, userTokens.getAccessToken());
        assertEquals(400, createOrder.extract().statusCode());
        assertEquals("Ingredient ids must be provided", createOrder.extract().path("message"));
    }
    @Test
    @DisplayName("Создание заказа c неверным хешем ингридиентов")
    public void orderCanNotBeCreatedWithWrongIngredients() {
        ArrayList wrongIngredients = new ArrayList<>();
        wrongIngredients.add(faker.hashCode());
        Order wrongOrder = new Order(wrongIngredients);
        ValidatableResponse createOrder = orderClient.createOrderWithAuth(wrongOrder, userTokens.getAccessToken());
        assertEquals(500, createOrder.extract().statusCode());
    }
}