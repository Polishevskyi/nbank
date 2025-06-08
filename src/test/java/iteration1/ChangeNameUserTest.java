package iteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.UpdateCustomerProfileRequest;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.Matchers.equalTo;

public class ChangeNameUserTest extends BaseTest {
    @Test
    public void userCanChangeNameWithValidData() {
        CreateUserRequest userRequest = AdminSteps.createUser();

        UpdateCustomerProfileRequest updateRequest = UpdateCustomerProfileRequest.builder()
                .name(RandomData.getUsername() + " " + RandomData.getUsername())
                .build();

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOK())
                .update(updateRequest);

        new CrudRequester(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get()
                .body("name", equalTo(updateRequest.getName()));
    }
}
