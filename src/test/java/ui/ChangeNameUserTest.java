package ui;

import com.codeborne.selenide.Configuration;
import extensions.UserExtension;
import generators.RandomData;
import models.CreateUserRequestModel;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(UserExtension.class)
public class ChangeNameUserTest {

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
    @DisplayName("User can change name with valid data")
    public void userCanChangeNameWithValidDataTest(CreateUserRequestModel userRequest, Long userId) {
        // ШАГИ ПО НАСТРОЙКЕ ОКРУЖЕНИЯ
        // ШАГ 1: пользователь уже создан и зарегистрирован через UserExtension
        // ШАГ 2: получаем заголовок авторизации пользователя для UI
        String userAuthHeader = new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOKSpec())
                .post(LoginUserRequestModel.builder().username(userRequest.getUsername())
                        .password(userRequest.getPassword()).build())
                .extract()
                .header("Authorization");

        // ШАГ 3: открываем базовый URL и устанавливаем токен авторизации в localStorage
        open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

        // ШАГ 4: переходим на страницу дашборда
        open("/dashboard");

        // ШАГИ ТЕСТА
        // ШАГ 5: кликаем на имя пользователя на дашборде, чтобы перейти в профиль
        $(byText(userRequest.getUsername())).click();

        // ШАГ 6: вводим новое имя
        String newName = RandomData.getUsername() + " " + RandomData.getUsername();
        $(By.cssSelector("input[placeholder=\"Enter new name\"]")).setValue(newName);

        // ШАГ 7: кликаем на кнопку "Сохранить изменения"
        $(By.cssSelector("button.btn.btn-primary.mt-3")).click();

        // ШАГ 8: проверяем сообщение об успешном изменении через UI (alert)
        Alert successAlert = switchTo().alert();
        assertThat(successAlert.getText()).contains("✅ Name updated successfully!");
        successAlert.accept();

        // ШАГ 9: проверяем, что имя было изменено на бэкенде через API
        String actualProfileName = UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword());

        // Нормализуем оба имени для сравнения (переводим в нижний регистр, удаляем запятые, восклицательные знаки и пробелы по краям)
        assertEquals(newName.toLowerCase().replace(",", "").trim(),
                actualProfileName.toLowerCase().replace(",", "").replace("!", "").trim());
    }
}
