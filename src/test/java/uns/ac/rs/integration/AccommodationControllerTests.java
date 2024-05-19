package uns.ac.rs.integration;

import static org.mockito.Mockito.*;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.MicroserviceCommunicator;
import uns.ac.rs.controller.AccommodationController;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccommodationControllerTests {

    @TestHTTPEndpoint(AccommodationController.class)
    @TestHTTPResource("create")
    URL createAccommodationEndpoint;

    @TestHTTPEndpoint(AccommodationController.class)
    @TestHTTPResource("my-accommodations")
    URL getHostsAccommodationsEndpoint;

    @TestHTTPEndpoint(AccommodationController.class)
    @TestHTTPResource("availability-period/create")
    URL createAvailabilityPeriod;

    @InjectMock
    private MicroserviceCommunicator microserviceCommunicator;

    @Test
    @Order(1)
    public void whenCreateAccommodationWithoutAuthorization_thenReturnUnauthorized() {
        doReturn(new GeneralResponse("", "401"))
                .when(microserviceCommunicator)
                .processResponse("http://localhost:8001/user-service/auth/authorize/host",
                        "GET",
                        "Bearer fake-jwt");
        String requestBody = "{" +
                "\"location\": {" +
                    "\"country\": \"Serbia\"," +
                    "\"city\": \"Subotica\"" +
                "}," +
                "\"accommodationFeatures\": [" +
                    "{" +
                        "\"feature\": \"Kitchen\"" +
                    "}," +
                    "{" +
                        "\"feature\": \"AC\"" +
                    "}" +
                "]," +
                "\"photographs\": [" +
                    "\"url1\"," +
                    "\"url2\"," +
                    "\"url3\"" +
                "]," +
                "\"minimumNoGuests\": 0," +
                "\"maximumNoGuests\": 10" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer fake-jwt")
                .body(requestBody)
        .when()
                .post(createAccommodationEndpoint)
        .then()
                .statusCode(401)
                .body("data", equalTo(""))
                .body("message", equalTo("401"));
    }


    @Test
    @Order(2)
    public void whenCreateAccommodationWithAuthorization_thenReturnCreatedWithAccommodation() {
        doReturn(new GeneralResponse("host@gmail.com", "200"))
                .when(microserviceCommunicator)
                .processResponse("http://localhost:8001/user-service/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");
        String requestBody = "{" +
                "\"location\": {" +
                "\"country\": \"Serbia\"," +
                "\"city\": \"Subotica\"" +
                "}," +
                "\"accommodationFeatures\": [" +
                "{" +
                "\"feature\": \"Kitchen\"" +
                "}," +
                "{" +
                "\"feature\": \"AC\"" +
                "}" +
                "]," +
                "\"photographs\": [" +
                "\"url1\"," +
                "\"url2\"," +
                "\"url3\"" +
                "]," +
                "\"minimumNoGuests\": 1," +
                "\"maximumNoGuests\": 10" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
                .body(requestBody)
        .when()
                .post(createAccommodationEndpoint)
        .then()
                .statusCode(201)
                .body("data.location.country", equalTo("Serbia"))
                .body("data.location.city", equalTo("Subotica"))
                .body("data.accommodationFeatures.size()", equalTo(2))
                .body("data.photographs.size()", equalTo(3))
                .body("data.minimumNoGuests", equalTo(1))
                .body("data.maximumNoGuests", equalTo(10))
                .body("data.hostEmail", equalTo("host@gmail.com"))
                .body("message", equalTo("Accommodation successfully created"));
    }

    @Test
    @Order(3)
    public void whenGetAccommodationsForHost_thenReturnHostsAccommodations() {
        doReturn(new GeneralResponse("host@gmail.com", "200"))
                .when(microserviceCommunicator)
                .processResponse("http://localhost:8001/user-service/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
        .when()
                .get(getHostsAccommodationsEndpoint)
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(1))
                .body("message", equalTo("Host's accommodations successfully retrieved"));
    }

    @Test
    @Order(4)
    public void whenCreateAvailabilityPeriod_thenReturnUpdatedAccommodation() {
        String requestBody = "{" +
                "\"startDate\": 1716156000000," +
                "\"endDate\": 1716328800000," +
                "\"accommodationId\": 1" +
                "}";

        doReturn(new GeneralResponse("host@gmail.com", "200"))
                .when(microserviceCommunicator)
                .processResponse("http://localhost:8001/user-service/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
                .body(requestBody)
        .when()
                .post(createAvailabilityPeriod)
        .then()
                .statusCode(201)
                .body("data.location.country", equalTo("Serbia"))
                .body("data.location.city", equalTo("Subotica"))
                .body("data.accommodationFeatures.size()", equalTo(2))
                .body("data.photographs.size()", equalTo(3))
                .body("data.minimumNoGuests", equalTo(1))
                .body("data.maximumNoGuests", equalTo(10))
                .body("data.hostEmail", equalTo("host@gmail.com"))
                .body("data.availabilityPeriods.size()", equalTo(1))
                .body("message", equalTo("Availability period successfully added"));
    }

    @Test
    @Order(5)
    public void whenCreateAvailabilityPeriodWithInvalidDateRange_thenReturnBadRequest() {
        String requestBody = "{" +
                "\"startDate\": 1716242400000," +
                "\"endDate\": 1716415200000," +
                "\"accommodationId\": 1" +
                "}";

        doReturn(new GeneralResponse("host@gmail.com", "200"))
                .when(microserviceCommunicator)
                .processResponse("http://localhost:8001/user-service/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
                .body(requestBody)
        .when()
                .post(createAvailabilityPeriod)
        .then()
                .statusCode(400)
                .body("data", equalTo(""))
                .body("message",equalTo("Provided dates aren't valid"));
    }


}

