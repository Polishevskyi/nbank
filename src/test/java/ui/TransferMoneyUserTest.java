package ui;

import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.models.DepositRequestModel;
import api.models.TransactionsResponseModel;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;

import java.util.stream.Stream;

public class TransferMoneyUserTest extends BaseUiTest {
    private static final float DEPOSIT_AMOUNT = 5000.0f;
    private static final float TRANSFER_AMOUNT = 10000.0f;

    @Test
    @UserSession
    @DisplayName("User can transfer money with correct data")
    public void userCanTransferMoneyWithCorrectDataTest(CreateUserRequestModel userRequest) {
        AccountsResponseModel sourceAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        AccountsResponseModel targetAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

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

        TransactionsResponseModel transfer = UserSteps.getTransactions(
                        userRequest.getUsername(), userRequest.getPassword(), sourceAccount.getId()).stream()
                .filter(t -> "TRANSFER_OUT".equals(t.getType()))
                .findFirst()
                .orElseThrow();

        softly.assertThat(transfer.getAmount()).isEqualTo(TRANSFER_AMOUNT);
        softly.assertThat(transfer.getType()).isEqualTo("TRANSFER_OUT");
        softly.assertThat(transfer.getRelatedAccountId().longValue()).isEqualTo(targetAccount.getId());
        softly.assertThat(transfer.getId()).isNotNull();
        softly.assertThat(transfer.getTimestamp()).isNotNull();
    }

    @Test
    @UserSession
    @DisplayName("User can not transfer money with amount greater than limit")
    public void userCanNotTransferMoneyWithAmountGreaterThanMaxTest(CreateUserRequestModel userRequest) {
        AccountsResponseModel sourceAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        AccountsResponseModel targetAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

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

    static Stream<Arguments> invalidTransferData() {
        return Stream.of(Arguments.of(TRANSFER_AMOUNT, "", "",
                        BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, "", "",
                        BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, "NonExistentUser", "1234567890",
                        BankAlert.NO_USER_FOUND_WITH_ACCOUNT_NUMBER.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, "User", "badformat",
                        BankAlert.NO_USER_FOUND_WITH_ACCOUNT_NUMBER.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, "User", "",
                        BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, "", "1234567890",
                        BankAlert.NO_USER_FOUND_WITH_ACCOUNT_NUMBER.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, null, null,
                        BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, "User", null,
                        BankAlert.FILL_ALL_FIELDS_AND_CONFIRM.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, null, "1234567890",
                        BankAlert.NO_USER_FOUND_WITH_ACCOUNT_NUMBER.getMessage()),
                Arguments.of(TRANSFER_AMOUNT, "User", "UserAccountNumber",
                        BankAlert.INVALID_TRANSFER.getMessage()));
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("invalidTransferData")
    @DisplayName("User cannot transfer money with invalid data")
    void userCannotTransferMoneyWithInvalidData(Float amount, String recipientName, String recipientAccountNumber,
                                                String expectedUiError, CreateUserRequestModel userRequest) {
        AccountsResponseModel sourceAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        String targetAccountNumber = "UserAccountNumber".equals(recipientAccountNumber) ? sourceAccount.getAccountNumber() : recipientAccountNumber;

        new UserDashboard().open().transferMoney();

        TransferPage transferPage = new TransferPage().selectSourceAccount(sourceAccount.getId().toString());

        if (recipientName != null) transferPage.enterRecipientName(recipientName);

        if (targetAccountNumber != null) transferPage.enterRecipientAccountNumber(targetAccountNumber);

        transferPage.enterAmount(amount)
                .clickConfirmCheck()
                .clickSendTransfer()
                .checkAlertMessageAndAccept(expectedUiError);

        UserSteps.verifyNoTransferTransactions(userRequest.getUsername(), userRequest.getPassword(), sourceAccount.getId());
    }
}