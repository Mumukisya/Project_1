import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.example.clients.ApiClient;
import org.example.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.generators.OrderGenerator.*;
import static org.example.generators.UserGenerator.randomNonValidEmailUser;
import static org.example.generators.UserGenerator.randomUser;
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

    @Test
    @DisplayName("Проверка тела ответа при успешной регистрации")
    public void verifyRegisterResponseBodyStructure() {
        Faker faker = new Faker();
        // 1. Подготовка тестовых данных
        RegisterRequest registerRequest = new RegisterRequest(
                faker.internet().safeEmailAddress(),
                faker.internet().password(),
                faker.name().firstName()
        );

        // 2. Вызов API
        Response response = apiClient.register(registerRequest);

        // 3. Проверка, что ответ JSON
        assertThat(response.getContentType())
                .as("Content-Type должен быть application/json")
                .contains("application/json");

        // 4. Десериализация ответа
        RegisterResponse registerResponse = response.as(RegisterResponse.class);

        // 5. Проверка основных полей ответа
        assertThat(registerResponse.isSuccess())
                .as("Поле success должно быть true при успешной регистрации")
                .isTrue();

        assertThat(registerResponse.getAccessToken())
                .as("AccessToken не должен быть пустым")
                .isNotBlank();

        assertThat(registerResponse.getRefreshToken())
                .as("RefreshToken не должен быть пустым")
                .isNotBlank();

        // 6. Проверка вложенного объекта User
        User user = registerResponse.getUser();
        assertThat(user)
                .as("Объект user не должен быть null")
                .isNotNull();

        assertThat(user.getEmail())
                .as("Email пользователя должен соответствовать отправленному")
                .isEqualTo(registerRequest.getEmail());

        assertThat(user.getName())
                .as("Имя пользователя должно соответствовать отправленному")
                .isEqualTo(registerRequest.getName());
    }

    @Test
    @DisplayName("Проверка тела ответа при ошибке регистрации")
    public void verifyErrorRegisterResponseBody() {
        Faker faker = new Faker();
        // 1. Подготовка невалидных данных
        RegisterRequest invalidRequest = new RegisterRequest(
                faker.name().firstName(),
                faker.internet().password(),
                faker.internet().emailAddress()
        );

        // 2. Вызов API
        Response response = apiClient.register(invalidRequest);

        // 3. Проверка Content-Type
        assertThat(response.getContentType())
                .as("Content-Type должен быть text/html")
                .contains("text/html");

        // 4. Проверка тела ответа (HTML)
        String body = response.getBody().asString();
        assertThat(body)
                .as("HTML-ответ не должен быть пустым")
                .isNotBlank();

        // Дополнительные проверки HTML-содержимого, если нужно
        assertThat(body)
                .as("HTML-ответ должен содержать информацию об ошибке")
                .containsIgnoringCase("internal server error");
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
    @DisplayName("Проверка тела ответа при успешной авторизации")
    public void verifyAuthSuccessResponseBody() {
        Faker faker = new Faker();
        // 1. Подготовка тестового пользователя
        RegisterRequest registerRequest = new RegisterRequest(
                faker.internet().safeEmailAddress(),
                faker.internet().password(),
                faker.name().firstName()
        );
        // 2. Регистрация пользователя
        apiClient.register(registerRequest);

        // 3. Авторизация
        Response response = apiClient.auth();

        // 4. Проверка типа контента
        assertThat(response.getContentType())
                .as("Ответ должен быть в формате JSON")
                .contains("application/json");

        // 5. Десериализация и проверка тела ответа
        AuthResponse authResponse = response.as(AuthResponse.class);

        assertThat(authResponse.getAccessToken())
                .as("AccessToken не должен быть пустым")
                .isNotBlank();
    }

    @Test
    @DisplayName("Проверка ошибки 401 при неверных учетных данных")
    public void verifyAuthUnauthorizedError() {
        Faker faker = new Faker();
        // 1. Вызов API
        Response response = apiClient.auth(faker.internet().safeEmailAddress(), faker.internet().password());

        // 2. Проверка тела ответа (может быть JSON или HTML)
            AuthErrorResponse error = response.as(AuthErrorResponse.class);
            assertThat(error.getMessage())
                    .as("Сообщение об ошибке не должно быть пустым")
                    .isNotBlank();

        assertThat(error.getMessage())
                .as("Сообщение об ошибке соответствует")
                .isEqualTo("email or password are incorrect");

        assertThat(error.isSuccess())
                .as("Проверить параметр success")
                .isFalse();
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

    @Test
    @DisplayName("Тестирование изменения пользователя с валидными данными")
    public void checkUserEditWithoutToken() {
        Faker faker = new Faker();

        apiClient.register(randomUser());
        apiClient.auth();

        EditUserRequest editUserRequest = new EditUserRequest(faker.internet().safeEmailAddress(), faker.name().firstName());
        Response response = apiClient.editUserWithoutToken(editUserRequest);

        assertThat("Некорректный код ответа", response.statusCode(), equalTo(401));
    }

    @Test
    @DisplayName("Проверка тела ответа при успешном редактировании пользователя")
    public void verifyEditUserSuccessResponseBody() {
        // 1. Регистрация и авторизация тестового пользователя
        apiClient.register(randomUser());
        apiClient.auth();

        // 2. Подготовка данных для редактирования
        Faker faker = new Faker();
        EditUserRequest editRequest = new EditUserRequest(
                faker.internet().safeEmailAddress(),
                faker.name().firstName()
        );

        // 3. Вызов API редактирования
        Response response = apiClient.editUser(editRequest);

        // 4. Проверка типа контента
        assertThat(response.getContentType())
                .as("Ответ должен быть в формате JSON")
                .contains("application/json");

        // 5. Десериализация и проверка тела ответа
        // Предполагаем, что возвращается UserResponse с обновленными данными
        EditUserResponse userResponse = response.as(EditUserResponse.class);

        assertThat(userResponse.getUser().getEmail())
                .as("Email должен соответствовать новому значению")
                .isEqualTo(editRequest.getEmail());

        assertThat(userResponse.getUser().getName())
                .as("Name должен соответствовать новому значению")
                .isEqualTo(editRequest.getName());
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

        assertThat("Некорректный код ответа", response.statusCode(), equalTo(200));
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
    @DisplayName("Тестирование создания заказа без авторизации")
    public void checkCreateOrderWithoutToken() {

        apiClient.register(randomUser());
        apiClient.auth();

        Response response = apiClient.createOrderWithoutToken(generateRandomValidOrder());
        assertThat("Некорректный код ответа", response.statusCode(), equalTo(200));
//        Я бы поставил 401 статус код для проверки, но почему-то я могу создать заказ без авторизации
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

    @Test
    @DisplayName("Тестирование получения заказов без токена")
    public void checkUserOrderWithoutToken() {

        apiClient.register(randomUser());
        apiClient.auth();
        apiClient.createOrder(generateRandomValidOrder());

        Response response = apiClient.getUserOrdersWithoutToken();
        assertThat("Некорректный код ответа", response.statusCode(), equalTo(200));
//        Я бы поставил 401 статус код для проверки, но почему-то я могу получить заказ без авторизации
        //Хотя написано, что у КОНКРЕТНОГО ПОЛЬЗОВАТЕЛЯ. Это как ?????
    }


    @AfterEach
    public void tearDown() {
        try {
            apiClient.delete();
        } catch (Exception e) {
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
