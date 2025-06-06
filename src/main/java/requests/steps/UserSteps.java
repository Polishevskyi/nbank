package requests.steps;

import io.restassured.specification.ResponseSpecification;
import models.*;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.concurrent.ThreadLocalRandom;

public class UserSteps {
    public static DepositResponse createAccount(CreateUserRequest userRequest) {
        DepositResponse depositResponse = new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        return depositResponse;
    }

    public static DepositRequest generateDeposit(DepositResponse account) {
        return DepositRequest.builder()
                .id(account.getId())
                .balance(Math.min(ThreadLocalRandom.current().nextFloat() * (Math.nextUp(5000.0f) - 1.0f) + 1.0f, 5000.0f))
                .build();
    }

    public static DepositResponse depositMoneyToAccount(DepositRequest depositRequest, CreateUserRequest userRequest, ResponseSpecification responseSpecs) {
        return new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                responseSpecs)
                .post(depositRequest);
    }

    public static TransferMoneyResponse transferMoneyToAccount(TransferMoneyRequest transferMoneyRequest, CreateUserRequest userRequest, ResponseSpecification responseSpecs) {
        return new ValidatedCrudRequester<TransferMoneyResponse>(
                RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                responseSpecs)
                .post(transferMoneyRequest);
    }
}
