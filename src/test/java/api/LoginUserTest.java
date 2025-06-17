package api;

import api.extensions.UserExtension;
import api.models.CreateUserRequestModel;
import api.models.LoginUserResponseModel;
import api.models.comparison.ModelAssertions;
import api.requests.steps.UserSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UserExtension.class)
public class LoginUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can generate auth token")
    public void adminCanGenerateAuthTokenTest() {
        UserSteps.login("admin", "admin");
    }

    @Test
    @DisplayName("User can generate auth token")
    public void userCanGenerateAuthTokenTest(CreateUserRequestModel userRequest, Long userId) {
        LoginUserResponseModel userResponse = UserSteps.loginAndGetResponse(userRequest.getUsername(), userRequest.getPassword());

        ModelAssertions.assertThatModels(userRequest, userResponse).match();
    }
}
