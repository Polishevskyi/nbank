package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public class AccountTransactionsRequester extends Request {
    public AccountTransactionsRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        return null;
    }

    public ValidatableResponse get(BaseModel model, Long id) {
        return given()
                .spec(requestSpecification)
                .get("/api/v1/accounts/" + id + "/transactions")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
