package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.UpdateCustomerProfileRequest;

import static io.restassured.RestAssured.given;

public class UpdateCustomerProfileRequester extends Request<UpdateCustomerProfileRequest> {
    public UpdateCustomerProfileRequester(RequestSpecification requestSpecification,
                                          ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(UpdateCustomerProfileRequest model) {
        return null;
    }

    public ValidatableResponse put(UpdateCustomerProfileRequest createdName) {
        return given()
                .spec(requestSpecification)
                .body(createdName)
                .log().body()
                .put("/api/v1/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
