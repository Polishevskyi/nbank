package ui;

import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.models.DepositRequestModel;
import api.models.DepositResponseModel;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositUserTest extends BaseUiTest {

    private static final float DEPOSIT_AMOUNT = 5000.0F;
    private static final float INVALID_DEPOSIT_AMOUNT = 5001.0F;

    @Test
    @UserSession
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

    @Test
    @UserSession
    @DisplayName("User can not add deposit with amount greater than 5000")
    public void userCanNotAddDepositWithAmountGreaterThanMaxTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest.getUsername(), userRequest.getPassword());

        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(
                userRequest.getUsername(),
                userRequest.getPassword());

        new UserDashboard().open().createNewAccount().depositMoney();

        new DepositPage()
                .selectAccount(String.valueOf(accountsResponse.getId()))
                .enterAmount(INVALID_DEPOSIT_AMOUNT)
                .clickDeposit()
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_AMOUNT_EXCEEDS_LIMIT.getMessage());

        DepositRequestModel apiDepositRequest = DepositRequestModel.builder()
                .id(accountsResponse.getId())
                .balance(DEPOSIT_AMOUNT)
                .build();

        DepositResponseModel apiDepositResponse = UserSteps.deposit(userRequest.getUsername(),
                userRequest.getPassword(), apiDepositRequest);

        assertThat(apiDepositResponse.getBalance()).isEqualTo(DEPOSIT_AMOUNT);
    }
}