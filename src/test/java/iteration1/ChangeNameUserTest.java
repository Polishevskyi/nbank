package iteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.UpdateCustomerProfileRequest;
import models.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequester;
import requests.UpdateCustomerProfileRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class ChangeNameUserTest extends BaseTest {

    @Test
    public void userCanChangeNameWithValidData() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
                .post(userRequest);

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name("New Name")
                .build();

        new UpdateCustomerProfileRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .put(updateRequest);
    }

    public static Stream<Arguments> invalidNameData() {
        return Stream.of(
                Arguments.of("AB", "Name must contain two words with letters only"),
                Arguments.of("This is a very long name that exceeds fifty characters",
                        "Name must contain two words with letters only"),
                Arguments.of("Invalid Name!@#$", "Name must contain two words with letters only"),
                Arguments.of("Иван abc", "Name must contain two words with letters only"),
                Arguments.of("", "Name must contain two words with letters only"));
    }

    @ParameterizedTest
    @MethodSource("invalidNameData")
    public void userCannotChangeNameWithInvalidData(String name, String expectedErrorMessage) {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
                .post(userRequest);

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(name)
                .build();

        new UpdateCustomerProfileRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(expectedErrorMessage))
                .put(updateRequest);
    }
}
