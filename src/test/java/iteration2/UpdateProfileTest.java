package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class UpdateProfileTest {

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }

    private String createUserAndGetAuthHeader(String password) {
        String username = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 9);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "%s",
                          "role": "USER"
                        }
                        """, username, password))
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);

        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "%s"
                        }
                        """, username, password))
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");
    }

    @Test
    public void positiveTestSuccessfulNameUpdate() {
        String password = "ProfilePass#1";
        String userAuthHeader = createUserAndGetAuthHeader(password);
        String newName = "John Doe";

        given()
                .header("Authorization", userAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, newName))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", Matchers.equalTo(newName));
    }

    public static Stream<Arguments> invalidNameData() {
        String expectedValidationMessage = "Name must contain two words with letters only";

        return Stream.of(
                Arguments.of("AB", HttpStatus.SC_BAD_REQUEST, expectedValidationMessage),
                Arguments.of("This is a very long name that exceeds fifty characters", HttpStatus.SC_BAD_REQUEST, expectedValidationMessage),
                Arguments.of("Invalid Name!@#$", HttpStatus.SC_BAD_REQUEST, expectedValidationMessage),
                Arguments.of("Иван abc", HttpStatus.SC_BAD_REQUEST, expectedValidationMessage),
                Arguments.of("", HttpStatus.SC_BAD_REQUEST, expectedValidationMessage)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNameData")
    public void negativeTestNameValidation(String name, int expectedStatusCode, String expectedBodyMessage) {
        String password = "ProfilePass#1";
        String userAuthHeader = createUserAndGetAuthHeader(password);

        given()
                .header("Authorization", userAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.TEXT)
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, name))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(expectedStatusCode)
                .body(Matchers.equalTo(expectedBodyMessage));
    }

    @Test
    public void negativeTestNameIsNotString() {
        String password = "ProfilePass#1";
        String userAuthHeader = createUserAndGetAuthHeader(password);
        int numericName = 12345;
        String expectedBodyMessage = "Name must contain two words with letters only";

        given()
                .header("Authorization", userAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.TEXT)
                .body(String.format("""
                        {
                          "name": %d
                        }
                        """, numericName))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo(expectedBodyMessage));
    }
}