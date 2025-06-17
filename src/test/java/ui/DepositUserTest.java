package ui;

import api.extensions.UserExtension;
import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.models.DepositRequestModel;
import api.models.DepositResponseModel;
import api.requests.steps.UserSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(UserExtension.class)
public class DepositUserTest extends BaseUiTest {

    private static final float DEPOSIT_AMOUNT = 2000.0F;

    @Test
    @DisplayName("User can add deposit with correct data")
    public void userCanAddDepositWithCorrectDataTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest.getUsername(), userRequest.getPassword());

        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(
                userRequest.getUsername(),
                userRequest.getPassword());

        new UserDashboard().open().createNewAccount().depositMoney();

        new DepositPage()
                .selectAccount(String.valueOf(accountsResponse.getId()))
                .enterAmount(DEPOSIT_AMOUNT)
                .clickDeposit()
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_SUCCESSFUL.getMessage());

        DepositRequestModel apiDepositRequest = DepositRequestModel.builder()
                .id(accountsResponse.getId())
                .balance(DEPOSIT_AMOUNT)
                .build();

        DepositResponseModel apiDepositResponse = UserSteps.deposit(userRequest.getUsername(),
                userRequest.getPassword(), apiDepositRequest);

        assertThat(apiDepositResponse.getBalance()).isEqualTo(DEPOSIT_AMOUNT + DEPOSIT_AMOUNT);
    }
}
