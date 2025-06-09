package requests.steps;

import generators.RandomModelGenerator;
import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {
    public static CreateUserRequestModel createUser() {
        CreateUserRequestModel userRequest =
                RandomModelGenerator.generate(CreateUserRequestModel.class);

        new ValidatedCrudRequester<CreateUserResponseModel>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        return userRequest;
    }
}
