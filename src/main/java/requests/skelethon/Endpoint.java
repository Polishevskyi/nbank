package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USERS(
            "/admin/users",
            CreateUserRequestModel.class,
            CreateUserResponseModel.class),

    LOGIN(
            "/auth/login",
            LoginUserRequestModel.class,
            LoginUserResponseModel.class),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            AccountsResponseModel.class),

    DEPOSIT(
            "/accounts/deposit",
            DepositRequestModel.class,
            DepositResponseModel.class),

    TRANSFER(
            "/accounts/transfer",
            TransferMoneyRequestModel.class,
            TransferMoneyResponseModel.class),

    TRANSACTIONS(
            "/accounts/{accountId}/transactions",
            BaseModel.class,
            TransactionsResponseModel.class),

    PROFILE(
            "/customer/profile",
            UpdateCustomerProfileRequestModel.class,
            UpdateCustomerProfileResponseModel.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
