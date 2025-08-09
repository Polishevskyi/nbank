package ui;

import api.BaseTest;
import api.models.CreateUserRequestModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = api.configs.Config.getProperty("uiRemote");
        Configuration.baseUrl = api.configs.Config.getProperty("uiBaseUrl");
        Configuration.browser = api.configs.Config.getProperty("browser");
        Configuration.browserSize = api.configs.Config.getProperty("browserSize");

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    public void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public void authAsUser(CreateUserRequestModel createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}
