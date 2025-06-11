package api;

import models.CreateUserRequestModel;
import models.LoginUserResponseModel;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;

public class LoginUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can generate auth token")
    public void adminCanGenerateAuthTokenTest() {
        UserSteps.login("admin", "admin");
    }

    @Test
    @DisplayName("User can generate auth token")
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequestModel userRequest = AdminSteps.createUser();

        LoginUserResponseModel userResponse = UserSteps.loginAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        ModelAssertions.assertThatModels(userRequest, userResponse).match();

        UserSteps.deleteUser(AdminSteps.getCreatedUserId());
    }
}
