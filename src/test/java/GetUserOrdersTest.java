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

@Epic("Работа с заказами")
@Feature("Получение заказов конкретного пользователя")
public class GetUserOrdersTest {
    static UserClient userClient;
    static OrderClient orderClient;
    static IngredientClient ingredientClient;
    User user;
    UserCredentials userCredentials;
    UserTokens userTokens;
    Order order;
    ArrayList ingredients;

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
    @DisplayName("Получение заказов конкретного авторизованного пользователя без сделанных заказов")
    public void authorizedUserWithoutCreatedOrderOrdersCanBeGot() {
        ValidatableResponse getOrders = orderClient.getOrdersOfUserWithAuth(userTokens.getAccessToken());
        assertEquals(200, getOrders.extract().statusCode());
        assertEquals(true, getOrders.extract().path("success"));
        assertEquals("[]", getOrders.extract().path("orders").toString());
    }
    @Test
    @DisplayName("Получение заказов конкретного авторизованного пользователя, который сделал 1 заказ")
    @Description("Дополнительная проверка того, что заказы передаются корректно")
    public void authorizedUserWithCreatedOrderOrdersCanBeGot() {
        ValidatableResponse getIngredients = ingredientClient.getIngredients();
        ingredients = new ArrayList<>();
        ingredients.add(getIngredients.extract().path("data[0]._id"));
        ingredients.add(getIngredients.extract().path("data[1]._id"));
        order = new Order(ingredients);
        ValidatableResponse createOrder = orderClient.createOrderWithAuth(order, userTokens.getAccessToken());
        String orderNumber = createOrder.extract().path("order.number").toString();
        ValidatableResponse getOrders = orderClient.getOrdersOfUserWithAuth(userTokens.getAccessToken());
        assertEquals(200, getOrders.extract().statusCode());
        assertEquals(true, getOrders.extract().path("success"));
        assertEquals(orderNumber, getOrders.extract().path("orders[0].number").toString());
    }
    @Test
    @DisplayName("Получение заказов конкретного пользователя без авторизации")
    public void unauthorizedUserOrdersCanNotBeGot() {
        ValidatableResponse getOrders = orderClient.getOrdersOfUserWithoutAuth();
        assertEquals(401, getOrders.extract().statusCode());
        assertEquals(false, getOrders.extract().path("success"));
        assertEquals("You should be authorised", getOrders.extract().path("message"));
    }
}