import com.github.javafaker.Faker;
import com.ya.*;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Epic("Работа с заказами")
@Feature("Созадние заказа")
public class CreateOrderTest {
    UserClient userClient;
    OrderClient orderClient;
    IngredientClient ingredientClient;
    User user;
    UserCredentials userCredentials;
    UserTokens userTokens;
    Order order;
    ArrayList ingredients;
    static Faker faker = new Faker();

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        userClient = new UserClient();
        userClient.createUser(user);
        ValidatableResponse loginTestUser = userClient.loginUser(userCredentials);
        userTokens = new UserTokens(loginTestUser.extract().path("refreshToken"), loginTestUser.extract().path("accessToken"));
        ingredientClient = new IngredientClient();
        ValidatableResponse getIngredients = ingredientClient.getIngredients();
        ingredients = new ArrayList<>();
        ingredients.add(getIngredients.extract().path("data[0]._id"));
        ingredients.add(getIngredients.extract().path("data[1]._id"));
        orderClient = new OrderClient();
        order = new Order(ingredients);
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
    @DisplayName("Создание заказа без авторизации")
    @Description("Для ревьюера: В документации явно не указано, какой ответ и код ответа должна давать система при создании заказа без авторизации," +
            "поэтому проверка создана из предположения, что код ответа должен быть 401 и сообщение You should be authorised")
    public void orderCanNotBeCreatedWithoutAuth() {
        orderClient = new OrderClient();
        ValidatableResponse createOrder = orderClient.createOrderWithoutAuth(order);
        assertEquals(401, createOrder.extract().statusCode());
        assertEquals("You should be authorised", createOrder.extract().path("message"));
    }
    @Test
    @DisplayName("Создание заказа c авторизацией и ингридиентами")
    public void orderCanBeCreatedWithAuth() {
        orderClient = new OrderClient();
        ValidatableResponse createOrder = orderClient.createOrderWithAuth(order, userTokens.getAccessToken());
        assertEquals(200, createOrder.extract().statusCode());
        assertTrue(createOrder.extract().path("name").toString().contains("бургер"));
    }
    @Test
    @DisplayName("Создание заказа без ингридиентов")
    public void orderCanNotBeCreatedWithoutIngredients() {
        orderClient = new OrderClient();
        ArrayList nullIngredients = new ArrayList<>();
        Order nullOrder = new Order(nullIngredients);
        ValidatableResponse createOrder = orderClient.createOrderWithAuth(nullOrder, userTokens.getAccessToken());
        assertEquals(400, createOrder.extract().statusCode());
        assertEquals("Ingredient ids must be provided", createOrder.extract().path("message"));
    }
    @Test
    @DisplayName("Создание заказа c неверным хешем ингридиентов")
    public void orderCanNotBeCreatedWithWrongIngredients() {
        orderClient = new OrderClient();
        ArrayList wrongIngredients = new ArrayList<>();
        wrongIngredients.add(faker.hashCode());
        Order wrongOrder = new Order(wrongIngredients);
        ValidatableResponse createOrder = orderClient.createOrderWithAuth(wrongOrder, userTokens.getAccessToken());
        assertEquals(500, createOrder.extract().statusCode());
    }
}