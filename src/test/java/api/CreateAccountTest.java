package api;

import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CreateAccountTest extends BaseTest {

    @Test
    @UserSession
    @DisplayName("User can create account")
    public void userCanCreateAccountTest(CreateUserRequestModel userRequest) {
        UserSteps.createAccount(userRequest.getUsername(), userRequest.getPassword());

        UserSteps.verifyUserExists(userRequest.getUsername());
    }
}
