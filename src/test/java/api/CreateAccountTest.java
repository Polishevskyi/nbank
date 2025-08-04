package api;

import api.models.AccountsResponseModel;
import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CreateAccountTest extends BaseTest {

    @Test
    @UserSession
    @DisplayName("User can create account")
    public void userCanCreateAccountTest(CreateUserRequestModel userRequest) {
        AccountsResponseModel accountResponse = UserSteps.createAccountAndGetResponse(userRequest.getUsername(),
                userRequest.getPassword());
        softly.assertThat(accountResponse).isNotNull();
        softly.assertThat(accountResponse.getId()).isNotNull();
        softly.assertThat(accountResponse.getAccountNumber()).isNotNull();
        softly.assertThat(accountResponse.getBalance()).isEqualTo(0.0f);
        UserSteps.verifyUserExists(userRequest.getUsername());
    }

    @Test
    @UserSession
    @DisplayName("User can get all accounts")
    public void userCanGetAllAccountsTest(CreateUserRequestModel userRequest) {
        UserSteps.createAccountAndGetResponse(userRequest.getUsername(), userRequest.getPassword());
        List<AccountsResponseModel> accounts = UserSteps.getAllAccounts(userRequest.getUsername(),
                userRequest.getPassword());
        softly.assertThat(accounts).isNotNull();
        softly.assertThat(accounts).isNotEmpty();
    }
}
