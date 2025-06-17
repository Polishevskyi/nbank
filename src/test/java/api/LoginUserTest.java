package api;

import extensions.UserExtension;
import models.CreateUserRequestModel;
import models.LoginUserResponseModel;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import requests.steps.UserSteps;

@ExtendWith(UserExtension.class)
public class LoginUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can generate auth token")
    public void adminCanGenerateAuthTokenTest() {
        UserSteps.login("admin", "admin");
    }

    @Test
    @DisplayName("User can generate auth token")
    public void userCanGenerateAuthTokenTest(CreateUserRequestModel userRequest) {
        LoginUserResponseModel userResponse = UserSteps.loginAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        ModelAssertions.assertThatModels(userRequest, userResponse).match();
    }
}
