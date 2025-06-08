package iteration1;

import models.CreateUserRequest;
import models.DepositRequest;
import models.DepositResponse;
import models.TransferMoneyRequest;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.ResponseSpecs;

public class TransferMoneyUserTest extends BaseTest {
    private static final float INITIAL_DEPOSIT = 1000.0f;
    private static final float TRANSFER_AMOUNT = 500.0f;

    @Test
    public void userCanTransferMoneyWithCorrectData() {
        CreateUserRequest userRequest = AdminSteps.createUser();
//        DepositResponse sourceAccount = UserSteps.createAccount(userRequest);
//        DepositResponse targetAccount = UserSteps.createAccount(userRequest);
//
//        DepositRequest depositRequest = DepositRequest.builder()
//                .id(sourceAccount.getId())
//                .balance(INITIAL_DEPOSIT)
//                .build();
//
//        UserSteps.depositMoneyToAccount(depositRequest, userRequest, ResponseSpecs.requestReturnsOK());
//
//        TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
//                .senderAccountId(sourceAccount.getId())
//                .receiverAccountId(targetAccount.getId())
//                .amount(TRANSFER_AMOUNT)
//                .build();
//
//        UserSteps.transferMoneyToAccount(transferRequest, userRequest, ResponseSpecs.requestReturnsOK());
    }
}
