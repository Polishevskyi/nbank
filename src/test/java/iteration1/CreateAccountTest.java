package iteration1;

import models.CreateUserRequestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.Matchers.hasItem;

public class CreateAccountTest extends BaseTest {

    @Test
    @DisplayName("User can create account")
    public void userCanCreateAccountTest() {
        CreateUserRequestModel userRequest = AdminSteps.createUser();

        new CrudRequester(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);

        new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.requestReturnsOKSpec())
                .get()
                .body("username", hasItem(userRequest.getUsername()));

        UserSteps.deleteUser(AdminSteps.getCreatedUserId());
    }
}
