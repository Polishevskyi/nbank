package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.AccountsResponse;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public class CreateAccountRequester extends Request {
    public CreateAccountRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        return given()
                .spec(requestSpecification)
                .post("/api/v1/accounts")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public AccountsResponse postWithAccountExtractData(BaseModel model) {
        return given()
                .spec(requestSpecification)
                .post("/api/v1/accounts")
                .then()
                .spec(responseSpecification)
                .extract()
                .as(AccountsResponse.class);
    }
}