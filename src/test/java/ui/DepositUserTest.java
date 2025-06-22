package ui;

import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.models.TransactionsResponseModel;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositUserTest extends BaseUiTest {

    static Stream<Arguments> validDepositAmounts() {
        return Stream.of(Arguments.of(1.0f),
                Arguments.of(100.0f),
                Arguments.of(4999.99f),
                Arguments.of(5000.0f));
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("validDepositAmounts")
    @DisplayName("User can add deposit with valid amount")
    void userCanAddDepositWithValidAmount(float amount, CreateUserRequestModel userRequest) {
        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(
                userRequest.getUsername(),
                userRequest.getPassword());

        UserSteps.depositViaUi(accountsResponse.getId(), amount);

        UserSteps.verifyDepositTransaction(
                userRequest.getUsername(),
                userRequest.getPassword(),
                accountsResponse.getId(),
                amount);

        TransactionsResponseModel deposit = UserSteps.getTransactions(
                        userRequest.getUsername(),
                        userRequest.getPassword(),
                        accountsResponse.getId())
                .stream()
                .filter(t -> "DEPOSIT".equals(t.getType()))
                .findFirst()
                .orElseThrow();

        softly.assertThat(deposit.getAmount()).isEqualTo(amount);
        softly.assertThat(deposit.getType()).isEqualTo("DEPOSIT");
        softly.assertThat(deposit.getId()).isNotNull();
        softly.assertThat(deposit.getTimestamp()).isNotNull();
    }

    static Stream<Arguments> invalidDepositAmounts() {
        return Stream.of(Arguments.of(0.0f, BankAlert.INVALID_DEPOSIT_AMOUNT.getMessage()),
                Arguments.of(-1.0f, BankAlert.INVALID_DEPOSIT_AMOUNT.getMessage()),
                Arguments.of(5000.01f, BankAlert.DEPOSIT_AMOUNT_EXCEEDS_LIMIT.getMessage()));
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("invalidDepositAmounts")
    @DisplayName("User can not add deposit with invalid amount")
    void userCanNotAddDepositWithInvalidAmount(Float amount, String expectedUiError, CreateUserRequestModel userRequest) {
        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(
                userRequest.getUsername(),
                userRequest.getPassword());

        new UserDashboard().open().depositMoney();

        new DepositPage()
                .selectAccount(String.valueOf(accountsResponse.getId()))
                .enterAmount(amount)
                .clickDeposit()
                .checkAlertMessageAndAccept(expectedUiError);

        UserSteps.verifyTransactions(
                userRequest.getUsername(),
                userRequest.getPassword(),
                accountsResponse.getId());
    }

    @Test
    @UserSession
    @DisplayName("User can deposit twice with the same amount and balance is correct")
    void userCanDepositTwiceWithSameAmountAndBalanceIsCorrect(CreateUserRequestModel userRequest) {
        float depositAmount = 5000.0f;

        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(
                userRequest.getUsername(),
                userRequest.getPassword());

        UserSteps.depositViaUi(accountsResponse.getId(), depositAmount);
        UserSteps.depositViaUi(accountsResponse.getId(), depositAmount);

        AccountsResponseModel updatedAccount = UserSteps
                .getAllAccounts(userRequest.getUsername(), userRequest.getPassword())
                .stream()
                .filter(acc -> acc.getId().equals(accountsResponse.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(updatedAccount.getBalance()).isEqualTo(depositAmount + depositAmount);
    }
}