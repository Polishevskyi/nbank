package ui;

import api.extensions.UserExtension;
import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(UserExtension.class)
public class CreateAccountTest extends BaseUiTest {

    @Test
    @DisplayName("User can create account")
    public void userCanCreateAccountTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest);

        new UserDashboard().open().createNewAccount();

        List<AccountsResponseModel> createdAccounts = new UserSteps(userRequest.getUsername(), userRequest.getPassword())
                .getAllAccounts();

        assertThat(createdAccounts).hasSize(1);

        new UserDashboard().checkAlertMessageAndAccept(
                BankAlert.NEW_ACCOUNT_CREATED.getMessage() + createdAccounts.getFirst().getAccountNumber());

        assertThat(createdAccounts.getFirst().getBalance()).isZero();
    }
}
