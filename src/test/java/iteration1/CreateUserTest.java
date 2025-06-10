package iteration1;

import generators.RandomModelGenerator;
import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can create user with correct data")
    public void adminCanCreateUserWithCorrectDataTest() {
        CreateUserRequestModel createUserRequest =
                RandomModelGenerator.generate(CreateUserRequestModel.class);

        CreateUserResponseModel createUserResponse = new ValidatedCrudRequester<CreateUserResponseModel>
                (RequestSpecs.adminSpec(),
                        Endpoint.ADMIN_USERS,
                        ResponseSpecs.entityWasCreatedSpec())
                .post(createUserRequest);

        ModelAssertions.assertThatModels(createUserRequest, createUserResponse).match();

        UserSteps.deleteUser(createUserResponse.getId());
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                Arguments.of("   ", "Password33$", "USER", "username", "Username cannot be blank"),
                Arguments.of("ab", "Password33$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("abc$", "Password33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("abc%", "Password33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots")
        );

    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    @DisplayName("Admin can not create user with invalid data")
    public void adminCanNotCreateUserWithInvalidDataTest(String username, String password, String role, String errorKey, String errorValue) {
        CreateUserRequestModel createUserRequest = CreateUserRequestModel.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.requestReturnsBadRequestSpec(errorKey, errorValue))
                .post(createUserRequest);
    }
}
