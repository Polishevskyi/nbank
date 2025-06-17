package ui;

import api.extensions.UserExtension;
import api.models.CreateUserRequestModel;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.AdminPanel;
import ui.pages.LoginPage;
import ui.pages.UserDashboard;

@ExtendWith(UserExtension.class)
public class LoginUserTest extends BaseUiTest {

    @Test
    @DisplayName("Admin can login with correct data")
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();

        new LoginPage().open().login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanel.class).getAdminPanelText().shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("User can login with correct data")
    public void userCanLoginWithCorrectDataTest(CreateUserRequestModel userRequest, Long userId) {
        new LoginPage().open().login(userRequest.getUsername(), userRequest.getPassword())
                .getPage(UserDashboard.class).getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
    }
}
