package requests.steps;

import generators.RandomModelGenerator;
import lombok.Getter;
import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {
    @Getter
    private static Long createdUserId;

    public static CreateUserRequestModel createUser() {
        CreateUserRequestModel userRequest = RandomModelGenerator.generate(CreateUserRequestModel.class);

        CreateUserResponseModel response = new ValidatedCrudRequester<CreateUserResponseModel>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(userRequest);

        createdUserId = response.getId();
        return userRequest;
    }
}
