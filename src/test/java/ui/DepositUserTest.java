package ui;

import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.util.List;
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
    @DisplayName("User can add deposit with valid amount (UI + API)")
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
    }

    static Stream<Arguments> invalidDepositAmounts() {
        return Stream.of(
                Arguments.of(0.0f, "❌ Please enter a valid amount."),
                Arguments.of(-1.0f, "❌ Please enter a valid amount."),
                Arguments.of(5000.01f, "❌ Please deposit less or equal to 5000$."),
                Arguments.of(999999.0f, "❌ Please deposit less or equal to 5000$."));
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("invalidDepositAmounts")
    @DisplayName("User can not add deposit with invalid amount (UI + API)")
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
    @DisplayName("User can deposit twice with the same amount and balance is correct (UI + API)")
    void userCanDepositTwiceWithSameAmountAndBalanceIsCorrect(CreateUserRequestModel userRequest) {
        float depositAmount = 5000.0f;

        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(
                userRequest.getUsername(),
                userRequest.getPassword());

        UserSteps.depositViaUi(accountsResponse.getId(), depositAmount);
        UserSteps.depositViaUi(accountsResponse.getId(), depositAmount);

        List<AccountsResponseModel> accounts = UserSteps.getAllAccounts(
                userRequest.getUsername(),
                userRequest.getPassword());

        AccountsResponseModel updatedAccount = accounts.stream()
                .filter(acc -> acc.getId().equals(accountsResponse.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(updatedAccount.getBalance()).isEqualTo(depositAmount + depositAmount);
    }
}