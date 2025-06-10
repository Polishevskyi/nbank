package iteration1;

import models.CreateUserRequestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;

public class CreateAccountTest extends BaseTest {

    @Test
    @DisplayName("User can create account")
    public void userCanCreateAccountTest() {
        CreateUserRequestModel userRequest = AdminSteps.createUser();
        UserSteps.createAccount(userRequest.getUsername(), userRequest.getPassword());
        UserSteps.verifyUserExists(userRequest.getUsername());
    }
}
