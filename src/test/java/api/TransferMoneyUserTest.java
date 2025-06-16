package api;

import extensions.UserExtension;
import models.*;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.UserSteps;

import java.util.stream.Stream;

@ExtendWith(UserExtension.class)
public class TransferMoneyUserTest extends BaseTest {
    private static final float DEPOSIT_AMOUNT = 5000.0f;
    private static final float TRANSFER_AMOUNT = 10000.0f;

    @Test
    @DisplayName("User can transfer money with correct data")
    public void userCanTransferMoneyWithCorrectDataTest(CreateUserRequestModel userRequest, Long userId) {
        AccountsResponseModel sourceAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        AccountsResponseModel targetAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(sourceAccount.getId())
                .balance(DEPOSIT_AMOUNT)
                .build();

        UserSteps.deposit(userRequest.getUsername(), userRequest.getPassword(), depositRequest);
        UserSteps.deposit(userRequest.getUsername(), userRequest.getPassword(), depositRequest);

        TransferMoneyRequestModel transferRequest = TransferMoneyRequestModel.builder()
                .senderAccountId(sourceAccount.getId())
                .receiverAccountId(targetAccount.getId())
                .amount(TRANSFER_AMOUNT)
                .build();

        TransferMoneyResponseModel transferResponse = UserSteps.transfer(userRequest.getUsername(), userRequest.getPassword(), transferRequest);

        UserSteps.verifyTransferTransactions(userRequest.getUsername(), userRequest.getPassword(),
                depositRequest.getId(), DEPOSIT_AMOUNT, TRANSFER_AMOUNT);

        ModelAssertions.assertThatModels(transferRequest, transferResponse).match();
    }

    private static Stream<Arguments> invalidTransferData() {
        return Stream.of(Arguments.of(0.0f, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(-1.0f, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(10001.0f, "Transfer amount cannot exceed 10000"));
    }

    @ParameterizedTest
    @MethodSource("invalidTransferData")
    @DisplayName("User can not transfer money with invalid data")
    public void userCannotTransferMoneyWithInvalidDataTest(Float amount, String errorMessage, CreateUserRequestModel userRequest, Long userId) {

        AccountsResponseModel sourceAccount = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(sourceAccount.getId())
                .balance(DEPOSIT_AMOUNT)
                .build();

        UserSteps.deposit(userRequest.getUsername(), userRequest.getPassword(), depositRequest);

        TransferMoneyRequestModel transferRequest = TransferMoneyRequestModel.builder()
                .senderAccountId(sourceAccount.getId())
                .receiverAccountId(sourceAccount.getId())
                .amount(amount)
                .build();

        UserSteps.transferWithError(userRequest.getUsername(), userRequest.getPassword(), transferRequest, errorMessage);

        UserSteps.verifyDepositTransaction(userRequest.getUsername(), userRequest.getPassword(), sourceAccount.getId(), DEPOSIT_AMOUNT);
    }
}
