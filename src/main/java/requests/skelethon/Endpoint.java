package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            AccountsResponseModel.class),

    ADMIN_USERS(
            "/admin/users",
            CreateUserRequestModel.class,
            CreateUserResponseModel.class),

    DELETE(
            "/admin/users/{accountId}",
            BaseModel.class,
            BaseModel.class),

    DEPOSIT(
            "/accounts/deposit",
            DepositRequestModel.class,
            DepositResponseModel.class),

    LOGIN(
            "/auth/login",
            LoginUserRequestModel.class,
            LoginUserResponseModel.class),

    PROFILE(
            "/customer/profile",
            UpdateCustomerProfileRequestModel.class,
            UpdateCustomerProfileResponseModel.class),

    TRANSACTIONS(
            "/accounts/{accountId}/transactions",
            BaseModel.class,
            TransactionsResponseModel.class),

    TRANSFER(
            "/accounts/transfer",
            TransferMoneyRequestModel.class,
            TransferMoneyResponseModel.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
