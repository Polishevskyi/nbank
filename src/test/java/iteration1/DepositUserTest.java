package iteration1;

import generators.RandomData;
import models.AccountsResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import models.UserRole;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.DepositRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class DepositUserTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(floats = {1.0f, 5000.0f, 50.50f})
    public void userCanAddDepositWithCorrectData(float balance) {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        AccountsResponse account = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .postWithAccountExtractData(null);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(balance)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);
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
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        AccountsResponse account = new CreateAccountRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreated())
                .postWithAccountExtractData(null);

        DepositRequest depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(balance)
                .build();

        new DepositRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(errorMessage))
                .post(depositRequest);
    }
}
