package iteration1;

import models.AccountsResponseModel;
import models.CreateUserRequestModel;
import models.DepositRequestModel;
import models.TransferMoneyRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class TransferMoneyUserTest extends BaseTest {
    private static final float INITIAL_DEPOSIT = 1000.0f;
    private static final float TRANSFER_AMOUNT = 500.0f;

    @Test
    public void userCanTransferMoneyWithCorrectData() {
        CreateUserRequestModel userRequest = AdminSteps.createUser();

        AccountsResponseModel sourceAccount = new ValidatedCrudRequester<AccountsResponseModel>(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);

        AccountsResponseModel targetAccount = new ValidatedCrudRequester<AccountsResponseModel>(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(sourceAccount.getId())
                .balance(INITIAL_DEPOSIT)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOKSpec())
                .post(depositRequest);

        TransferMoneyRequestModel transferRequest = TransferMoneyRequestModel.builder()
                .senderAccountId(sourceAccount.getId())
                .receiverAccountId(targetAccount.getId())
                .amount(TRANSFER_AMOUNT)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOKSpec())
                .post(transferRequest);

        new CrudRequester(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOKSpec())
                .get(depositRequest.getId())
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
        CreateUserRequestModel userRequest = AdminSteps.createUser();

        AccountsResponseModel sourceAccount = new ValidatedCrudRequester<AccountsResponseModel>(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(sourceAccount.getId())
                .balance(INITIAL_DEPOSIT)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOKSpec())
                .post(depositRequest);

        TransferMoneyRequestModel transferRequest = TransferMoneyRequestModel.builder()
                .senderAccountId(sourceAccount.getId())
                .receiverAccountId(sourceAccount.getId())
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(transferRequest);

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOKSpec())
                .get(sourceAccount.getId())
                .body("$", hasSize(1))
                .body("find { it.type == 'DEPOSIT' }.amount", equalTo(depositRequest.getBalance()));
    }
}
