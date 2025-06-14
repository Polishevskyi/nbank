package ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import extensions.UserExtension;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Map;

import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(UserExtension.class)
public class DepositUserTest {
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
    @DisplayName("User can add deposit with correct data via UI and then via API")
    public void userCanAddDepositWithCorrectDataTest(CreateUserRequestModel userRequest, Long userId) {
        // ШАГИ ПО НАСТРОЙКЕ ОКРУЖЕНИЯ
        // ШАГ 1: пользователь уже создан и авторизован через UserExtension
        // ШАГ 2: админ создает пользователя
        // ШАГ 3: пользователь логинится в банке

        String userAuthHeader = new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOKSpec())
                .post(LoginUserRequestModel.builder().username(userRequest.getUsername())
                        .password(userRequest.getPassword()).build())
                .extract()
                .header("Authorization");

        // ШАГ 4: создаем аккаунт через API, чтобы он был доступен в UI
        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(userRequest.getUsername(),
                userRequest.getPassword());

        Selenide.open("/");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        Selenide.open("/dashboard");

        // ШАГИ ТЕСТА
        // ШАГ 5: пользователь переходит на страницу депозита и вносит деньги через UI (первый депозит)
        $(Selectors.byText("💰 Deposit Money")).click();
        $(Selectors.byCssSelector(".form-control.account-selector")).shouldBe(visible);
        $(Selectors.byCssSelector(
                String.format(".form-control.account-selector option[value='%s']", accountsResponse.getId())))
                .shouldBe(visible); // Ожидаем появления конкретной опции
        $(Selectors.byCssSelector(".form-control.account-selector"))
                .selectOptionByValue(String.valueOf(accountsResponse.getId())); // Выбираем аккаунт по ID
        $(Selectors.byCssSelector(
                String.format(".form-control.account-selector option[value='%s']", accountsResponse.getId())))
                .shouldBe(selected); // Проверяем, что опция действительно выбрана
        $(Selectors.byCssSelector(".form-control[placeholder='Enter amount']")).setValue(String.valueOf(2000.0F));
        $(Selectors.byText("💵 Deposit")).click();

        // ШАГ 6: проверяем, что депозит через UI был успешным, и подтверждаем попап
        Alert depositAlert = switchTo().alert();
        assertThat(depositAlert.getText()).contains("Successfully deposited");
        depositAlert.accept();

        // ШАГ 7: выполняем второй депозит через API на тот же счет
        float apiDepositAmount = 2000.0F;
        DepositRequestModel apiDepositRequest = DepositRequestModel.builder()
                .id(accountsResponse.getId())
                .balance(apiDepositAmount)
                .build();

        DepositResponseModel apiDepositResponse = UserSteps.deposit(userRequest.getUsername(),
                userRequest.getPassword(), apiDepositRequest);

        // Проверяем, что общий баланс после API-депозита равен сумме UI и API депозитов
        assertThat(apiDepositResponse.getBalance()).isEqualTo(2000.0F + apiDepositAmount);
    }
}
