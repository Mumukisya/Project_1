import com.github.javafaker.Faker;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.example.clients.ApiClient;
import org.example.models.CreateOrderRequest;
import org.example.models.EditUserRequest;
import org.example.models.RegisterRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.stream.Stream;

import static org.example.generators.UserGenerator.*;
import static org.example.generators.OrderGenerator.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ApiTests {


    private ApiClient apiClient = new ApiClient();

    @ParameterizedTest(name = "Регистрация пользователя с {2} и ожидаемым ответом: {1}")
    @MethodSource("registerData")
    @DisplayName("Тестирование регистрации пользователя")
    public void checkUserRegisterWithSuccess(RegisterRequest registerRequest, int statusCode, String forTestName) {
        Response response = apiClient.register(registerRequest);

        assertThat("Некорректный код ответа", response.statusCode(), equalTo(statusCode));
    }

    @ParameterizedTest(name = "Авторизация пользователя с {2} и ожидаемым ответом: {1}")
    @MethodSource("authData")
    @DisplayName("Тестирование авторизации пользователя с валидными данными")
    public void checkUserAuthWithSuccess(RegisterRequest registerRequest, int statusCode, String forTestName) {
        apiClient.register(registerRequest);
        Response response = apiClient.auth();

        assertThat("Некорректный код ответа", response.statusCode(), equalTo(statusCode));
    }

    @Test
    @DisplayName("Тестирование изменения пользователя с валидными данными")
    public void checkUserEditWithSuccess() {
        Faker faker = new Faker();

        apiClient.register(randomUser());
        apiClient.auth();

        EditUserRequest editUserRequest = new EditUserRequest(faker.internet().safeEmailAddress(), faker.name().firstName());
        Response response = apiClient.editUser(editUserRequest);

        assertThat("Некорректный код ответа", response.statusCode(), equalTo(200));
    }

    @ParameterizedTest
    @MethodSource("data")
    @DisplayName("Тестирование изменения пользователя с невалидными данными" +
            "Я бы сделал нормально, но апи не работает нормально" +
            "Я могу поменять значения на нули" +
            "Могу ввести невалидный email")
    public void checkUserEditWithFailure(EditUserRequest editUserRequest) {

        apiClient.register(randomUser());
        apiClient.auth();

        Response response = apiClient.editUser(editUserRequest);

        assertThat("Некорректный код ответа", response.statusCode(), equalTo(400));
    }

    @ParameterizedTest(name = "Создание заказа {2} и ожидаемым ответом: {1}")
    @MethodSource("orderData")
    @DisplayName("Тестирование создания заказа")
    public void checkCreateOrderWithSuccess(CreateOrderRequest createOrderRequest, int statusCode, String forTestName) {

        apiClient.register(randomUser());
        apiClient.auth();

        Response response = apiClient.createOrder(createOrderRequest);
        assertThat("Некорректный код ответа", response.statusCode(), equalTo(statusCode));
    }

    @Test
    @DisplayName("Тестирование получения заказов")
    public void checkUserOrderWithSuccess() {

        apiClient.register(randomUser());
        apiClient.auth();
        apiClient.createOrder(generateRandomValidOrder());

        Response response = apiClient.getUserOrders();
        assertThat("Некорректный код ответа", response.statusCode(), equalTo(200));
    }


    @AfterEach
    public void tearDown() {
        try {
            apiClient.delete();
        }catch (Exception e) {
            System.out.println("User not found, nothing to delete: " + e.getMessage());
        }
    }

    private static Stream<Arguments> registerData() {
        return Stream.of(
                Arguments.of(randomUser(), 200, "валидными данными"),
                Arguments.of(randomNonValidEmailUser(), 500, "невалидными данными"),
                Arguments.of(new RegisterRequest(), 403, "пустыми данными")
        );
    }

    private static Stream<Arguments> authData() {
        return Stream.of(
                Arguments.of(randomUser(), 200, "валидными данными"),
                Arguments.of(randomNonValidEmailUser(), 401, "невалидными данными"),
                Arguments.of(new RegisterRequest(), 401, "пустыми данными")
        );
    }

    private static Stream<Arguments> orderData() {
        return Stream.of(
                Arguments.of(generateRandomValidOrder(), 200, "c валидными данными"),
                Arguments.of(generateOrder(0, 1, 1), 500, "без булки (имитируя Frontend он блокирует возможность создавать бургер без булки)"),
                Arguments.of(generateOrder(0, 0, 0), 400, "без ингредиентов"),
                Arguments.of(generateNonValidOrder(5), 500, "с невалидными данными")
        );
    }

    private static Stream<Arguments> data() {
        Faker faker = new Faker();
        return Stream.of(
                Arguments.of(new EditUserRequest(null, null)),
                Arguments.of((new EditUserRequest(faker.name().firstName(), faker.internet().safeEmailAddress())))
        );
    }
}
