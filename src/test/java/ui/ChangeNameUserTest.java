package ui;

import api.generators.RandomData;
import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.ProfilePage;
import ui.pages.UserDashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeNameUserTest extends BaseUiTest {

    @Test
    @UserSession
    @DisplayName("User can change name with valid data")
    public void userCanChangeNameWithValidDataTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest);

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        String newName = RandomData.getUsername() + " " + RandomData.getUsername();

        new ProfilePage()
                .enterNewName(newName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

        new ProfilePage().open();

        assertEquals(newName, new ProfilePage().getProfileNameText());

        String actualProfileName = UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword());

        assertEquals(newName, actualProfileName);
    }

    @Test
    @UserSession
    @DisplayName("User can not change name to one word")
    public void userCanNotChangeNameToOneWordTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest);

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        String oldName = new ProfilePage().getProfileNameText();

        String invalidName = RandomData.getUsername();

        new ProfilePage()
                .enterNewName(invalidName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_INVALID.getMessage());

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        assertEquals(oldName, new ProfilePage().getProfileNameText());

        assertEquals(oldName, "Noname");
    }

    @Test
    @UserSession
    @DisplayName("User can not change name to the same value")
    public void userCanNotChangeNameToSameValueTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest);

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        String newName = RandomData.getUsername() + " " + RandomData.getUsername();

        new ProfilePage()
                .enterNewName(newName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        new ProfilePage()
                .enterNewName(newName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_SAME_AS_CURRENT.getMessage());

        assertEquals(newName, new ProfilePage().getProfileNameText());

        String actualProfileName = UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword());

        assertEquals(newName, actualProfileName);
    }
}