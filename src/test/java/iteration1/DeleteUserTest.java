package iteration1;

import generators.RandomData;
import generators.RandomModelGenerator;
import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.Matchers.equalTo;

public class DeleteUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can delete user with correct data")
    public void adminCanCreateUserWithCorrectDataTest() {
        CreateUserRequestModel userRequest = RandomModelGenerator.generate(CreateUserRequestModel.class);

        CreateUserResponseModel createUser = new ValidatedCrudRequester<CreateUserResponseModel>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(userRequest);

        String expectedMessage = String.format(ResponseSpecs.USER_DELETED_MESSAGE, createUser.getId());

        new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.DELETE,
                ResponseSpecs.requestReturnsOKSpec())
                .delete(createUser.getId())
                .body(equalTo(expectedMessage));
    }

    @Test
    @DisplayName("Admin can not delete non-existent user")
    public void adminCannotDeleteNonExistentUserTest() {
        Long nonExistentUserId = RandomData.getRandomId();
        String expectedMessage = String.format(ResponseSpecs.USER_NOT_FOUND_MESSAGE, nonExistentUserId);

        new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.DELETE,
                ResponseSpecs.requestReturnsNotFoundSpec(expectedMessage))
                .delete(nonExistentUserId)
                .body(equalTo(expectedMessage));
    }
}
