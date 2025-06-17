package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.CreateUserResponseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import lombok.Getter;

import java.util.List;

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

    public static List<CreateUserResponseModel> getAllUsers() {
        return new ValidatedCrudRequester<CreateUserResponseModel>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.requestReturnsOKSpec()).getAll(CreateUserResponseModel[].class);
    }
}
