package ui;

import api.generators.RandomData;
import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.ProfilePage;
import ui.pages.UserDashboard;

import java.util.stream.Stream;

public class ChangeNameUserTest extends BaseUiTest {

//    @Test
    @UserSession
    @DisplayName("User can change name with valid data")
    void userCanChangeNameWithValidDataTest(CreateUserRequestModel userRequest) {
        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        String userName = RandomData.getUsername() + " " + RandomData.getUsername();

        new ProfilePage()
                .enterNewName(userName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        softly.assertThat(new ProfilePage().getProfileNameText()).isEqualTo(userName);
        softly.assertThat(UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword())).isEqualTo(userName);
    }

    static Stream<Arguments> invalidNameCases() {
        return Stream.of(Arguments.of(((RandomData.getUsername() + " ").repeat(6).trim()) + " " + RandomData.getUsername(),
                        BankAlert.NAME_INVALID.getMessage()),
                Arguments.of(RandomData.getUsername().substring(0, 2) + " " + RandomData.getUsername().substring(0, 2),
                        BankAlert.NAME_INVALID.getMessage()),
                Arguments.of(RandomData.getUsername(), BankAlert.NAME_INVALID.getMessage()),
                Arguments.of("", BankAlert.NAME_INVALID.getMessage()),
                Arguments.of("@#$% ^&*", BankAlert.NAME_INVALID.getMessage()));
    }

//    @ParameterizedTest
    @UserSession
    @MethodSource("invalidNameCases")
    @DisplayName("User can not change name with invalid data")
    void userCanNotChangeNameWithInvalidDataTest(String userName, String expectedAlert, CreateUserRequestModel userRequest) {
        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        new ProfilePage()
                .enterNewName(userName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(expectedAlert);

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        softly.assertThat(new ProfilePage().getProfileNameText()).isEqualTo("Noname");
        softly.assertThat(UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword())).isEqualTo(null);
    }

//    @Test
    @UserSession
    @DisplayName("User can not change name to the same data")
    void userCanNotChangeNameToSameDataTest(CreateUserRequestModel userRequest) {
        String userName = RandomData.getUsername() + " " + RandomData.getUsername();

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        new ProfilePage()
                .enterNewName(userName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

        new UserDashboard().open().clickUsernameTitle(userRequest.getUsername());

        new ProfilePage()
                .enterNewName(userName)
                .clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.NAME_SAME_AS_CURRENT.getMessage());

        softly.assertThat(new ProfilePage().open().getProfileNameText()).isEqualTo(userName);
        softly.assertThat(UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword())).isEqualTo(userName);
    }
}