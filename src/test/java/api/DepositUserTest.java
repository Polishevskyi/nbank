package api;

import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.models.DepositRequestModel;
import api.models.DepositResponseModel;
import api.models.comparison.ModelAssertions;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class DepositUserTest extends BaseTest {

    @ParameterizedTest
    @UserSession
    @ValueSource(floats = {1.0f, 5000.0f, 50.50f})
    @DisplayName("User can add deposit with correct data")
    public void userCanAddDepositWithCorrectDataTest(float balance, CreateUserRequestModel userRequest) {
        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(accountsResponse.getId())
                .balance(balance)
                .build();

        DepositResponseModel depositResponse = UserSteps.deposit(userRequest.getUsername(), userRequest.getPassword(), depositRequest);

        ModelAssertions.assertThatModels(depositRequest, depositResponse).match();
    }

    public static Stream<Arguments> userIncorrectData() {
        return Stream.of(Arguments.of(-1.0f, "Invalid account or amount"),
                Arguments.of(0.0f, "Invalid account or amount"),
                Arguments.of(5001.0f, "Deposit amount exceeds the 5000 limit"),
                Arguments.of(9999999999999.0f, "Deposit amount exceeds the 5000 limit"));
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("userIncorrectData")
    @DisplayName("User can not add deposit with incorrect data")
    public void userCanNotAddDepositWithIncorrectDataTest(float balance, String errorMessage, CreateUserRequestModel userRequest) {
        AccountsResponseModel accountsResponse = UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(accountsResponse.getId())
                .balance(balance)
                .build();

        UserSteps.depositWithError(userRequest.getUsername(), userRequest.getPassword(), depositRequest, errorMessage);

        UserSteps.verifyTransactions(userRequest.getUsername(), userRequest.getPassword(), depositRequest.getId());
    }
}
