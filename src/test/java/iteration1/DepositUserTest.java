package iteration1;

import models.CreateUserRequest;
import models.DepositRequest;
import models.DepositResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class DepositUserTest extends BaseTest {

    @Test
    public void userCanAddDepositWithCorrectData() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        DepositResponse depositResponse = UserSteps.createAccount(userRequest);
        DepositRequest depositRequest = UserSteps.generateDeposit(depositResponse);
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
    }
}
