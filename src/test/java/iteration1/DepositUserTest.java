package iteration1;

import models.CreateUserRequest;
import models.DepositRequest;
import models.DepositResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

public class DepositUserTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(floats = {1.0f, 5000.0f, 50.50f})
    public void userCanAddDepositWithCorrectData(float balance) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        DepositResponse depositResponse = UserSteps.createAccount(userRequest);
        DepositRequest depositRequest = UserSteps.generateDeposit(depositResponse, balance);
        UserSteps.depositMoneyToAccount(depositRequest, userRequest, ResponseSpecs.requestReturnsOK());
    }

    public static Stream<Arguments> userIncorrectData() {
        return Stream.of(Arguments.of(-1.0f, "Invalid account or amount"),
                Arguments.of(0.0f, "Invalid account or amount"),
                Arguments.of(5001.0f, "Deposit amount exceeds the 5000 limit"),
                Arguments.of(9999999999999.0f, "Deposit amount exceeds the 5000 limit"));
    }

    @ParameterizedTest
    @MethodSource("userIncorrectData")
    public void userCanNotAddDepositWithIncorrectData(float balance, String errorMessage) {
        CreateUserRequest userRequest = AdminSteps.createUser();
        DepositResponse depositResponse = UserSteps.createAccount(userRequest);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(depositResponse.getId())
                .balance(balance)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(errorMessage))
                .post(depositRequest);

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOK())
                .get(depositRequest.getId())
                .body("amount", equalTo(new ArrayList<>()));
    }
}
