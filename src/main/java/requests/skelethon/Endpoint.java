package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USERS(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            DepositResponse.class),

    DEPOSIT(
            "/accounts/deposit",
            DepositRequest.class,
            DepositResponse.class),

    TRANSFER(
            "/accounts/transfer",
            TransferMoneyRequest.class,
            TransferMoneyResponse.class),

    TRANSACTIONS(
            "/accounts/{accountId}/transactions",
            BaseModel.class,
            TransactionsResponse.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
