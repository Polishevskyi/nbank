package ui;

import api.extensions.UserExtension;
import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.models.DepositRequestModel;
import api.requests.steps.UserSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.BankAlert;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;

@ExtendWith(UserExtension.class)
public class TransferMoneyUserTest extends BaseUiTest {
    private static final float INITIAL_DEPOSIT = 1000.0f;
    private static final float TRANSFER_AMOUNT = 500.0f;

    @Test
    @DisplayName("User can transfer money with correct data")
    public void userCanTransferMoneyWithCorrectDataTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest);

        AccountsResponseModel sourceAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(),
                userRequest.getPassword());

        AccountsResponseModel targetAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(),
                userRequest.getPassword());

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(sourceAccount.getId())
                .balance(INITIAL_DEPOSIT)
                .build();

        UserSteps.deposit(userRequest.getUsername(), userRequest.getPassword(), depositRequest);

        new UserDashboard().open().transferMoney();

        new TransferPage()
                .selectSourceAccount(sourceAccount.getId().toString())
                .enterRecipientName(userRequest.getUsername())
                .enterRecipientAccountNumber(targetAccount.getAccountNumber())
                .enterAmount(TRANSFER_AMOUNT)
                .clickConfirmCheck()
                .clickSendTransfer()
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_SUCCESSFUL.getMessage());

        UserSteps.verifyTransferTransactions(userRequest.getUsername(), userRequest.getPassword(),
                sourceAccount.getId(), INITIAL_DEPOSIT, TRANSFER_AMOUNT);
    }
}
