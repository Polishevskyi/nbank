package iteration1;

import generators.RandomData;
import models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.*;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class TransferMoneyUserTest extends BaseTest {
    private static final float INITIAL_DEPOSIT = 1000.0f;

    @Test
    public void userCanTransferMoneyWithCorrectData() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        AccountsResponse sourceAccount = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .postWithAccountExtractData(null);

        AccountsResponse targetAccount = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .postWithAccountExtractData(null);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(sourceAccount.getId())
                .balance(INITIAL_DEPOSIT)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(sourceAccount.getId())
                .receiverAccountId(targetAccount.getId())
                .amount(500.0f)
                .build();

        new TransferMoneyRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(transferRequest);

        new AccountTransactionsRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null, depositRequest.getId())
                .body("$", hasSize(2))
                .body("find { it.type == 'DEPOSIT' }.amount", equalTo(depositRequest.getBalance()))
                .body("find { it.type == 'TRANSFER_OUT' }.amount", equalTo(transferRequest.getAmount()));
    }

    private static Stream<Arguments> invalidTransferData() {
        return Stream.of(Arguments.of(0.0f, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(-50.0f, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(999999.0f, "Transfer amount cannot exceed 10000"));
    }

    @ParameterizedTest
    @MethodSource("invalidTransferData")
    public void userCannotTransferMoneyWithInvalidData(Float amount, String errorMessage) {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        AccountsResponse sourceAccount = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .postWithAccountExtractData(null);

        AccountsResponse targetAccount = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .postWithAccountExtractData(null);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(sourceAccount.getId())
                .balance(INITIAL_DEPOSIT)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);

        TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                .senderAccountId(sourceAccount.getId())
                .receiverAccountId(targetAccount.getId())
                .amount(amount)
                .build();

        new TransferMoneyRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(errorMessage))
                .post(transferRequest);

        new AccountTransactionsRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .get(null, sourceAccount.getId())
                .body("$", hasSize(1))
                .body("find { it.type == 'DEPOSIT' }.amount", equalTo(depositRequest.getBalance()));
    }
}
