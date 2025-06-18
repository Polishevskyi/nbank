package ui;

import api.extensions.UserExtension;
import api.generators.RandomData;
import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.BankAlert;
import ui.pages.ProfilePage;
import ui.pages.UserDashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(UserExtension.class)
public class ChangeNameUserTest extends BaseUiTest {

    @Test
    @DisplayName("User can change name with valid data")
    public void userCanChangeNameWithValidDataTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest);

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        String newName = RandomData.getUsername() + " " + RandomData.getUsername();
        new ProfilePage()
                .enterNewName(newName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

        String actualProfileName = UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword());

        assertEquals(newName.toLowerCase().replace(",", "").trim(),
                actualProfileName.toLowerCase().replace(",", "").replace("!", "").trim());
    }

    @Test
    @DisplayName("User can not change name to one word")
    public void userCanNotChangeNameToOneWordTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest);

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        new ProfilePage()
                .enterNewName(RandomData.getUsername())
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_INVALID.getMessage());
    }
}
