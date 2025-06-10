package requests.steps;

import models.UpdateCustomerProfileRequestModel;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.hamcrest.Matchers.hasItem;

public class UserSteps {
    public static void deleteUser(Long userId) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE,
                ResponseSpecs.requestReturnsOKSpec())
                .delete(userId)
                .extract()
                .response()
                .body()
                .asString();
    }

    public static void updateProfile(String username, String password,
            UpdateCustomerProfileRequestModel updateRequest) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOKSpec())
                .put(updateRequest);
    }

    public static void updateProfileWithError(String username, String password,
            UpdateCustomerProfileRequestModel updateRequest, String expectedErrorMessage) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsBadRequestSpec(expectedErrorMessage))
                .put(updateRequest);
    }

    public static String getProfile(String username, String password) {
        return new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOKSpec())
                .get()
                .extract()
                .path("name");
    }

    public static void createAccount(String username, String password) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);
    }

    public static void verifyUserExists(String username) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.requestReturnsOKSpec())
                .get()
                .body("username", hasItem(username));
    }
}
