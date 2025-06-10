package requests.steps;

import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {
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
}
