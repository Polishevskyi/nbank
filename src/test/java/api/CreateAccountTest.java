package api;

import extensions.UserExtension;
import models.CreateUserRequestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import requests.steps.UserSteps;

@ExtendWith(UserExtension.class)
public class CreateAccountTest extends BaseTest {

    @Test
    @DisplayName("User can create account")
    public void userCanCreateAccountTest(CreateUserRequestModel userRequest, Long userId) {
        UserSteps.createAccount(userRequest.getUsername(), userRequest.getPassword());

        UserSteps.verifyUserExists(userRequest.getUsername());
    }
}
