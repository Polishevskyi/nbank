package iteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.UpdateCustomerProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

public class ChangeNameUserTest extends BaseTest {
    @Test
    public void userCanChangeNameWithValidData() {
        CreateUserRequest userRequest = AdminSteps.createUser();

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(RandomData.getUsername() + " " + RandomData.getUsername())
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOK())
                .put(updateRequest);

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get()
                .body("name", equalTo(updateRequest.getName()));
    }

    public static Stream<Arguments> invalidNameData() {
        return Stream.of(Arguments.of("AB", "Name must contain two words with letters only"),
                Arguments.of("This is a very long name that exceeds fifty characters", "Name must contain two words with letters only"),
                Arguments.of("Invalid Name!@#$", "Name must contain two words with letters only"),
                Arguments.of("Иван abc", "Name must contain two words with letters only"),
                Arguments.of("", "Name must contain two words with letters only"));
    }

    @ParameterizedTest
    @MethodSource("invalidNameData")
    public void userCannotChangeNameWithInvalidData(String name, String expectedErrorMessage) {
        CreateUserRequest userRequest = AdminSteps.createUser();

        String initialName = new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .path("name");

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(name)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsBadRequest(expectedErrorMessage))
                .put(updateRequest);

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get()
                .body("name", equalTo(initialName));
    }
}
