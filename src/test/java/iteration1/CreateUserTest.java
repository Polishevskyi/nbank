package iteration1;

import models.CreateUserRequestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can create user with correct data")
    public void adminCanCreateUserWithCorrectDataTest() {
        CreateUserRequestModel createUserRequest = AdminSteps.createUser();

        UserSteps.verifyUserExists(createUserRequest.getUsername());

        UserSteps.deleteUser(AdminSteps.getCreatedUserId());
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(Arguments.of("   ", "Password33$", "USER", "username", "Username cannot be blank"),
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

        AdminSteps.createUserWithError(createUserRequest, errorKey, errorValue);
    }
}
