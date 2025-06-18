package api.requests.steps;

import api.models.*;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class UserSteps {
    private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<AccountsResponseModel> getAllAccounts() {
        return new ValidatedCrudRequester<AccountsResponseModel>(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOKSpec()).getAll(AccountsResponseModel[].class);
    }

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

    public static void updateProfile(String username, String password, UpdateCustomerProfileRequestModel updateRequest) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOKSpec())
                .put(updateRequest);
    }

    public static void updateProfileWithError(String username, String password, UpdateCustomerProfileRequestModel updateRequest, String expectedErrorMessage) {
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

    public static AccountsResponseModel createAccountAndGetResponse(String username, String password) {
        return new ValidatedCrudRequester<AccountsResponseModel>(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);
    }

    public static DepositResponseModel deposit(String username, String password, DepositRequestModel depositRequest) {
        return new ValidatedCrudRequester<DepositResponseModel>(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOKSpec())
                .post(depositRequest);
    }

    public static void depositWithError(String username, String password, DepositRequestModel depositRequest, String errorMessage) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(depositRequest);
    }

    public static void verifyTransactions(String username, String password, Long accountId) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOKSpec())
                .get(accountId)
                .body("amount", equalTo(new ArrayList<>()));
    }

    public static void verifyUserExists(String username) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.requestReturnsOKSpec())
                .get()
                .body("username", hasItem(username));
    }

    public static void login(String username, String password) {
        new ValidatedCrudRequester<LoginUserResponseModel>(RequestSpecs.unAuthSpec(), Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOKSpec())
                .post(LoginUserRequestModel.builder()
                        .username(username)
                        .password(password)
                        .build());
    }

    public static LoginUserResponseModel loginAndGetResponse(String username, String password) {
        return new ValidatedCrudRequester<LoginUserResponseModel>(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOKSpec())
                .post(LoginUserRequestModel.builder()
                        .username(username)
                        .password(password)
                        .build());
    }

    public static TransferMoneyResponseModel transfer(String username, String password, TransferMoneyRequestModel transferRequest) {
        return new ValidatedCrudRequester<TransferMoneyResponseModel>(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOKSpec())
                .post(transferRequest);
    }

    public static void transferWithError(String username, String password, TransferMoneyRequestModel transferRequest, String errorMessage) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequestSpec(errorMessage))
                .post(transferRequest);
    }

    public static void verifyTransferTransactions(String username, String password, Long accountId, float depositAmount, float transferAmount) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOKSpec())
                .get(accountId)
                .body("find { it.type == 'DEPOSIT' }.amount", equalTo(depositAmount))
                .body("find { it.type == 'TRANSFER_OUT' }.amount", equalTo(transferAmount));
    }

    public static void verifyDepositTransaction(String username, String password, Long accountId, float depositAmount) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOKSpec())
                .get(accountId)
                .body("$", hasSize(1))
                .body("find { it.type == 'DEPOSIT' }.amount", equalTo(depositAmount));
    }

    public static void verifyNoTransferTransactions(String username, String password, Long accountId) {
        new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOKSpec())
                .get(accountId)
                .body("findAll { it.type == 'TRANSFER_OUT' }", empty());
    }
}
