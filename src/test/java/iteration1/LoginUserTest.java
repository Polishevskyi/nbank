package iteration1;

import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import models.LoginUserRequestModel;
import models.LoginUserResponseModel;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can generate auth token")
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequestModel userRequest = LoginUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        new ValidatedCrudRequester<CreateUserResponseModel>(RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOKSpec())
                .post(userRequest);
    }

    @Test
    @DisplayName("User can generate auth token")
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequestModel userRequest = AdminSteps.createUser();

        LoginUserResponseModel userResponse = new ValidatedCrudRequester<LoginUserResponseModel>(RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOKSpec())
                .post(LoginUserRequestModel.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).build());

        ModelAssertions.assertThatModels(userRequest, userResponse).match();
    }
}
