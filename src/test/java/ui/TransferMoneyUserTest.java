package ui;

import com.codeborne.selenide.Configuration;
import extensions.UserExtension;
import models.AccountsResponseModel;
import models.CreateUserRequestModel;
import models.DepositRequestModel;
import models.LoginUserRequestModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Map;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(UserExtension.class)
public class TransferMoneyUserTest {
    private static final float INITIAL_DEPOSIT = 1000.0f;
    private static final float TRANSFER_AMOUNT = 500.0f;

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.0.106:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    @Test
    @DisplayName("User can transfer money with correct data")
    public void userCanTransferMoneyWithCorrectDataTest(CreateUserRequestModel userRequest, Long userId) {
        // ШАГИ ПО НАСТРОЙКЕ ОКРУЖЕНИЯ
        // ШАГ 1: пользователь уже создан и авторизован через UserExtension
        // ШАГ 2: создаем исходящий счет через API
        AccountsResponseModel sourceAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(),
                userRequest.getPassword());

        // ШАГ 3: создаем целевой счет (для того же пользователя) через API
        AccountsResponseModel targetAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(),
                userRequest.getPassword());

        // ШАГ 4: вносим начальный депозит на исходящий счет через API
        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(sourceAccount.getId())
                .balance(INITIAL_DEPOSIT)
                .build();

        UserSteps.deposit(userRequest.getUsername(), userRequest.getPassword(), depositRequest);

        // ШАГ 5: получаем заголовок авторизации пользователя для UI
        String userAuthHeader = new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOKSpec())
                .post(LoginUserRequestModel.builder().username(userRequest.getUsername())
                        .password(userRequest.getPassword()).build())
                .extract()
                .header("Authorization");

        // ШАГ 6: открываем базовый URL и устанавливаем токен авторизации в localStorage
        open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        // ШАГ 7: переходим на страницу дашборда
        open("/dashboard");

        // ШАГИ ТЕСТА
        // ШАГ 8: пользователь переходит на страницу перевода
        $(byText("🔄 Make a Transfer")).click();

        // ШАГ 9: выбираем исходящий счет в UI
        $(By.cssSelector("select.form-control.account-selector"))
                .selectOptionByValue(sourceAccount.getId().toString());

        // ШАГ 10: вводим имя пользователя получателя (того же пользователя)
        $(By.cssSelector("input[placeholder=\"Enter recipient name\"]"))
                .setValue(userRequest.getUsername());

        // ШАГ 11: вводим номер счета получателя (второго счета того же пользователя)
        $(By.cssSelector("input[placeholder=\"Enter recipient account number\"]"))
                .setValue(targetAccount.getAccountNumber());

        // ШАГ 12: вводим сумму перевода
        $(By.cssSelector("input[placeholder=\"Enter amount\"]")).setValue(String.valueOf(TRANSFER_AMOUNT));

        // ШАГ 13: подтверждаем правильность деталей
        $("#confirmCheck").click();

        // ШАГ 14: отправляем перевод
        $(byText("🚀 Send Transfer")).click();

        // ШАГ 15: проверяем сообщение об успешном переводе через UI
        Alert transferAlert = switchTo().alert();
        assertThat(transferAlert.getText()).contains("Successfully transferred");
        transferAlert.accept();

        // ШАГ 16: проверяем транзакции на бэкенде через API
        UserSteps.verifyTransferTransactions(userRequest.getUsername(), userRequest.getPassword(),
                sourceAccount.getId(), INITIAL_DEPOSIT, TRANSFER_AMOUNT);
    }
}
