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
    private static final float DEPOSIT_AMOUNT = 5000.0f;
    private static final float TRANSFER_AMOUNT = 10000.0f;

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
                .balance(DEPOSIT_AMOUNT)
                .build();

        UserSteps.deposit(userRequest.getUsername(), userRequest.getPassword(), depositRequest);
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
                sourceAccount.getId(), DEPOSIT_AMOUNT, TRANSFER_AMOUNT);
    }

    @Test
    @DisplayName("User can not transfer money with amount greater than limit")
    public void userCanNotTransferMoneyWithAmountGreaterThanMaxTest(CreateUserRequestModel userRequest) {
        authAsUser(userRequest);

        AccountsResponseModel sourceAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(),
                userRequest.getPassword());

        AccountsResponseModel targetAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(),
                userRequest.getPassword());

        new UserDashboard().open().transferMoney();

        new TransferPage()
                .selectSourceAccount(sourceAccount.getId().toString())
                .enterRecipientName(userRequest.getUsername())
                .enterRecipientAccountNumber(targetAccount.getAccountNumber())
                .enterAmount(TRANSFER_AMOUNT + 1)
                .clickConfirmCheck()
                .clickSendTransfer()
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_AMOUNT_EXCEEDS_LIMIT.getMessage());

        UserSteps.verifyNoTransferTransactions(userRequest.getUsername(), userRequest.getPassword(),
                sourceAccount.getId());
    }
}
