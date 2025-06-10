package iteration1;

import generators.RandomData;
import models.CreateUserRequestModel;
import models.UpdateCustomerProfileRequestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeNameUserTest extends BaseTest {

    @Test
    @DisplayName("User can change name with valid data")
    public void userCanChangeNameWithValidDataTest() {
        CreateUserRequestModel userRequest = AdminSteps.createUser();

        UpdateCustomerProfileRequestModel updateRequest = UpdateCustomerProfileRequestModel.builder()
                .name(RandomData.getUsername() + " " + RandomData.getUsername())
                .build();

        UserSteps.updateProfile(userRequest.getUsername(), userRequest.getPassword(), updateRequest);

        assertEquals(updateRequest.getName(), UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword()));

        UserSteps.deleteUser(AdminSteps.getCreatedUserId());
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
    @DisplayName("User can not change name with invalid data")
    public void userCannotChangeNameWithInvalidDataTest(String name, String expectedErrorMessage) {
        CreateUserRequestModel userRequest = AdminSteps.createUser();

        UpdateCustomerProfileRequestModel updateRequest = UpdateCustomerProfileRequestModel.builder()
                .name(name)
                .build();

        UserSteps.updateProfileWithError(userRequest.getUsername(), userRequest.getPassword(), updateRequest, expectedErrorMessage);

        String actualName = UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword());
        assertEquals(UserSteps.getProfile(userRequest.getUsername(), userRequest.getPassword()), actualName);

        UserSteps.deleteUser(AdminSteps.getCreatedUserId());
    }
}
