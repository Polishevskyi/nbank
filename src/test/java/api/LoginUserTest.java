package api;

import api.models.CreateUserRequestModel;
import api.models.LoginUserResponseModel;
import api.models.comparison.ModelAssertions;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can generate auth token")
    public void adminCanGenerateAuthTokenTest() {
        UserSteps.login("admin", "admin");
    }

    @Test
    @UserSession
    @DisplayName("User can generate auth token")
    public void userCanGenerateAuthTokenTest(CreateUserRequestModel userRequest) {
        LoginUserResponseModel userResponse = UserSteps.loginAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        ModelAssertions.assertThatModels(userRequest, userResponse).match();
    }
}
