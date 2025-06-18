package api;

import api.extensions.UserExtension;
import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UserExtension.class)
public class CreateAccountTest extends BaseTest {

    @Test
    @DisplayName("User can create account")
    public void userCanCreateAccountTest(CreateUserRequestModel userRequest) {
        UserSteps.createAccount(userRequest.getUsername(), userRequest.getPassword());

        UserSteps.verifyUserExists(userRequest.getUsername());
    }
}
