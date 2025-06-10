package iteration1;

import generators.RandomData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;

public class DeleteUserTest extends BaseTest {

    @Test
    @DisplayName("Admin can delete user with correct data")
    public void adminCanCreateUserWithCorrectDataTest() {
        AdminSteps.createUser();
        AdminSteps.deleteUserWithMessage(AdminSteps.getCreatedUserId());
    }

    @Test
    @DisplayName("Admin can not delete non-existent user")
    public void adminCannotDeleteNonExistentUserTest() {
        AdminSteps.deleteNonExistentUser(RandomData.getRandomId());
    }
}
