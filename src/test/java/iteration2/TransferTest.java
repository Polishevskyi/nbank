package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class TransferTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }

    private String createUserAndGetAuthHeader(String username, String password) {
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

    private Integer createAccountAndGetId(String userAuthHeader) {
        return given()
                .header("Authorization", userAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");
    }

    private void depositMoney(String userAuthHeader, Integer accountId, double amount) {
        given()
                .header("Authorization", userAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "id": %d,
                          "balance": %f
                        }
                        """, accountId, amount))
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void successfulTransferTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);
        Integer senderAccountId = createAccountAndGetId(senderAuthHeader);
        depositMoney(senderAuthHeader, senderAccountId, 200.0);

        String receiverUsername = "Receiver_" + UUID.randomUUID().toString().substring(0, 3);
        String receiverPassword = "ReceiverPass#1";
        String receiverAuthHeader = createUserAndGetAuthHeader(receiverUsername, receiverPassword);
        Integer receiverAccountId = createAccountAndGetId(receiverAuthHeader);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": 50.0
                        }
                        """, senderAccountId, receiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void negativeTransferAmountIsZeroTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);
        Integer senderAccountId = createAccountAndGetId(senderAuthHeader);
        depositMoney(senderAuthHeader, senderAccountId, 200.0);

        String receiverUsername = "Receiver_" + UUID.randomUUID().toString().substring(0, 3);
        String receiverPassword = "ReceiverPass#1";
        String receiverAuthHeader = createUserAndGetAuthHeader(receiverUsername, receiverPassword);
        Integer receiverAccountId = createAccountAndGetId(receiverAuthHeader);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": 0
                        }
                        """, senderAccountId, receiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void negativeTransferAmountIsNegativeTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);
        Integer senderAccountId = createAccountAndGetId(senderAuthHeader);
        depositMoney(senderAuthHeader, senderAccountId, 200.0);

        String receiverUsername = "Receiver_" + UUID.randomUUID().toString().substring(0, 3);
        String receiverPassword = "ReceiverPass#1";
        String receiverAuthHeader = createUserAndGetAuthHeader(receiverUsername, receiverPassword);
        Integer receiverAccountId = createAccountAndGetId(receiverAuthHeader);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": -50
                        }
                        """, senderAccountId, receiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void negativeTransferAmountIsNotANumberTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);
        Integer senderAccountId = createAccountAndGetId(senderAuthHeader);
        depositMoney(senderAuthHeader, senderAccountId, 200.0);

        String receiverUsername = "Receiver_" + UUID.randomUUID().toString().substring(0, 3);
        String receiverPassword = "ReceiverPass#1";
        String receiverAuthHeader = createUserAndGetAuthHeader(receiverUsername, receiverPassword);
        Integer receiverAccountId = createAccountAndGetId(receiverAuthHeader);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": "abc"
                        }
                        """, senderAccountId, receiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void negativeTransferAmountExceedsAvailableBalanceTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);
        Integer senderAccountId = createAccountAndGetId(senderAuthHeader);
        depositMoney(senderAuthHeader, senderAccountId, 200.0);

        String receiverUsername = "Receiver_" + UUID.randomUUID().toString().substring(0, 3);
        String receiverPassword = "ReceiverPass#1";
        String receiverAuthHeader = createUserAndGetAuthHeader(receiverUsername, receiverPassword);
        Integer receiverAccountId = createAccountAndGetId(receiverAuthHeader);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": 999999
                        }
                        """, senderAccountId, receiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void negativeTransferReceiverAccountDoesNotExistTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);
        Integer senderAccountId = createAccountAndGetId(senderAuthHeader);
        depositMoney(senderAuthHeader, senderAccountId, 200.0);
        int nonExistentReceiverAccountId = 999999;
        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d,
                          "amount": 50.0
                        }
                        """, senderAccountId, nonExistentReceiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void negativeTransferSenderAccountIdIsNotANumberTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);

        String receiverUsername = "Receiver_" + UUID.randomUUID().toString().substring(0, 3);
        String receiverPassword = "ReceiverPass#1";
        String receiverAuthHeader = createUserAndGetAuthHeader(receiverUsername, receiverPassword);
        Integer receiverAccountId = createAccountAndGetId(receiverAuthHeader);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": "abc",
                          "receiverAccountId": %d,
                          "amount": 50.0
                        }
                        """, receiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void negativeTransferMissingSenderAccountIdTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);

        String receiverUsername = "Receiver_" + UUID.randomUUID().toString().substring(0, 3);
        String receiverPassword = "ReceiverPass#1";
        String receiverAuthHeader = createUserAndGetAuthHeader(receiverUsername, receiverPassword);
        Integer receiverAccountId = createAccountAndGetId(receiverAuthHeader);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "receiverAccountId": %d,
                          "amount": 50.0
                        }
                        """, receiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void negativeTransferMissingReceiverAccountIdTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);
        Integer senderAccountId = createAccountAndGetId(senderAuthHeader);
        depositMoney(senderAuthHeader, senderAccountId, 200.0);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "amount": 50.0
                        }
                        """, senderAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void negativeTransferMissingAmountTest() {
        String senderUsername = "Sender_" + UUID.randomUUID().toString().substring(0, 3);
        String senderPassword = "SenderPass#1";
        String senderAuthHeader = createUserAndGetAuthHeader(senderUsername, senderPassword);
        Integer senderAccountId = createAccountAndGetId(senderAuthHeader);
        depositMoney(senderAuthHeader, senderAccountId, 200.0);

        String receiverUsername = "Receiver_" + UUID.randomUUID().toString().substring(0, 3);
        String receiverPassword = "ReceiverPass#1";
        String receiverAuthHeader = createUserAndGetAuthHeader(receiverUsername, receiverPassword);
        Integer receiverAccountId = createAccountAndGetId(receiverAuthHeader);

        given()
                .header("Authorization", senderAuthHeader)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "senderAccountId": %d,
                          "receiverAccountId": %d
                        }
                        """, senderAccountId, receiverAccountId))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
