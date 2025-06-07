package iteration1;

import generators.RandomData;
import models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.DepositRequester;
import requests.TransferMoneyRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

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
    }

    private static Stream<Arguments> invalidTransferData() {
        return Stream.of(Arguments.of(1L, 2L, 0.0f, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(1L, 2L, -50.0f, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(1L, 2L, 999999.0f, "Transfer amount cannot exceed 10000"),
                Arguments.of(999L, 2L, 50.0f, "Unauthorized access to account"),
                Arguments.of(1L, 999L, 50.0f, "Unauthorized access to account"),
                Arguments.of(1L, 1L, 50.0f, "Invalid transfer: insufficient funds or invalid accounts"));
    }

    @ParameterizedTest
    @MethodSource("invalidTransferData")
    public void userCannotTransferMoneyWithInvalidData(Long senderAccountId, Long receiverAccountId, Float amount, String expectedErrorMessage) {
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
    }
}
