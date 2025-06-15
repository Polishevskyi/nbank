package requests.steps;

import generators.RandomModelGenerator;
import lombok.Getter;
import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.Matchers.equalTo;

public class AdminSteps {
    @Getter
    private static Long createdUserId;

    public static CreateUserRequestModel createUser() {
        CreateUserRequestModel userRequest = RandomModelGenerator.generate(CreateUserRequestModel.class);

        CreateUserResponseModel response = new ValidatedCrudRequester<CreateUserResponseModel>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(userRequest);

        createdUserId = response.getId();
        return userRequest;
    }

    public static void createUserWithError(CreateUserRequestModel userRequest, String errorKey, String errorValue) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.requestReturnsBadRequestSpec(errorKey, errorValue))
                .post(userRequest);
    }

    public static void deleteUserWithMessage(Long userId) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE,
                ResponseSpecs.requestReturnsOKSpec())
                .delete(userId)
                .body(equalTo(String.format(ResponseSpecs.USER_DELETED_MESSAGE, userId)));
    }

    public static void deleteNonExistentUser(Long userId) {
        String expectedMessage = String.format(ResponseSpecs.USER_NOT_FOUND_MESSAGE, userId);
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE,
                ResponseSpecs.requestReturnsNotFoundSpec(expectedMessage))
                .delete(userId)
                .body(equalTo(expectedMessage));
    }
}
