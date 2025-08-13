package org.example.clients;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.example.models.*;

import static io.restassured.RestAssured.given;

public class ApiClient {

    private static final String API_AUTH = "/api/auth/login";
    private static final String API_USER = "/api/auth/user";
    private static final String API_ORDERS = "/api/orders";
    private static final String API_GET_INGRIDIENTS = "/api/ingredients";
    private static final String API_REGISTER = "/api/auth/register";

    private String token;
    private String login;
    private String password;
    private String name;

    public ApiClient() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Step("Создание пользователя")
    public Response register(RegisterRequest registerRequest) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(registerRequest)
                .post(API_REGISTER);
        if (response.getContentType().contains("application/json")) {
            token = response.as(RegisterResponse.class).getAccessToken();
            login = registerRequest.getEmail();
            password = registerRequest.getPassword();
            name = registerRequest.getName();
        } else {
            token = null;
        }
        return response;
    }

    @Step("Удаление пользователя")
    public void delete() {
        given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .delete(API_USER);
    }

    @Step("Авторизация пользователем")
    public Response auth() {
        AuthRequest authRequest = new AuthRequest(login, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(authRequest)
                .post(API_AUTH);
        token = response.as(AuthResponse.class).getAccessToken();
        return response;
    }

    @Step("Авторизация пользователем")
    public Response auth(String login, String password) {
        AuthRequest authRequest = new AuthRequest(login, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(authRequest)
                .post(API_AUTH);
        token = response.as(AuthResponse.class).getAccessToken();
        return response;
    }

    @Step("Изменение пользователя")
    public Response editUser(EditUserRequest editUserRequest) {
        Response response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .body(editUserRequest)
                .patch(API_USER);
        return response;
    }
    @Step("Создание заказа")
    public Response createOrder(CreateOrderRequest createOrderRequest) {
        Response response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .body(createOrderRequest)
                .post(API_ORDERS);
        return response;
    }

    public Response getUserOrders() {
        Response response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .get(API_GET_INGRIDIENTS);
        return response;
    }

    public Response getUserOrdersWithoutToken() {
        Response response = given()
                .header("Content-type", "application/json")
                .get(API_GET_INGRIDIENTS);
        return response;
    }
}