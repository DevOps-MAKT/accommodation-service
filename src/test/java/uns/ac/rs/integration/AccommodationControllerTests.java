package uns.ac.rs.integration;

import static org.mockito.Mockito.*;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.MicroserviceCommunicator;
import uns.ac.rs.config.IntegrationConfig;
import uns.ac.rs.controller.AccommodationController;
import uns.ac.rs.dto.request.AccommodationForm;
import uns.ac.rs.dto.response.ReservationResponseDTO;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    @TestHTTPResource("change-availability-and-price-info")
    URL changeAvailabilityAndPriceInfoEndpoint;

    @TestHTTPEndpoint(AccommodationController.class)
    @TestHTTPResource("filter?country=Serbia&city=Subotica&noGuests=7&startDate=1715724000000&endDate=1715810400000")
    URL filterWithValidArgumentsEndpoint;

    @TestHTTPEndpoint(AccommodationController.class)
    @TestHTTPResource("filter?country=Serbia&city=Subotica&noGuests=22&startDate=1715724000000&endDate=1715810400000")
    URL filterWithInvalidArgumentsEndpoint;

    @TestHTTPEndpoint(AccommodationController.class)
    @TestHTTPResource("deactivate-hosts-accommodations/host@gmail.com")
    URL deactivateHostsAccommodationsEndpoint;

    @TestHTTPEndpoint(AccommodationController.class)
    @TestHTTPResource("retrieve-min-accommodations")
    URL retrieveMinAccommodationEndpoint;

    @InjectMock
    private MicroserviceCommunicator microserviceCommunicator;

    @Autowired
    private IntegrationConfig config;

    @Test
    @Order(1)
    public void whenCreateAccommodationWithoutAuthorization_thenReturnUnauthorized() {
        doReturn(new GeneralResponse("", "401"))
                .when(microserviceCommunicator)
                .processResponse(config.userServiceAPI() + "/auth/authorize/host",
                        "GET",
                        "Bearer fake-jwt");

        byte[] fileContent = "dummyImageContent".getBytes();

        given()
                .contentType("multipart/form-data")
                .multiPart("location", "Subotica, Serbia")
                .multiPart("tags", "Kitchen,AC")
                .multiPart("fileName", "dummyFileName.jpg")
                .multiPart("file", "dummyFileName.jpg", fileContent, "image/jpeg")
                .header("Authorization", "Bearer fake-jwt")
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
                .processResponse(config.userServiceAPI() + "/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");

        byte[] fileContent = "dummyImageContent".getBytes();

        given()
                .header("Authorization", "Bearer good-jwt")
                .contentType("multipart/form-data")
                .multiPart("location", "Subotica, Serbia")
                .multiPart("tags", "Kitchen,AC")
                .multiPart("fileName", "dummyFileName.jpg")
                .multiPart("name", "some-accommodation")
                .multiPart("minGuests", "1")
                .multiPart("maxGuests", "10")
                .multiPart("file", "dummyFileName.jpg", fileContent, "image/jpeg")
                .header("Authorization", "Bearer fake-jwt")
        .when()
                .post(createAccommodationEndpoint)
        .then()
                .statusCode(201)
                .body("data.location.country", equalTo("Serbia"))
                .body("data.location.city", equalTo("Subotica"))
                .body("data.accommodationFeatures.size()", equalTo(2))
                .body("data.photographURL", equalTo(""))
                .body("data.minimumNoGuests", equalTo(1))
                .body("data.maximumNoGuests", equalTo(10))
                .body("data.hostEmail", equalTo("host@gmail.com"))
                .body("data.name", equalTo("some-accommodation"))
                .body("message", equalTo("Accommodation successfully created"));
    }

    @Test
    @Order(3)
    public void whenGetAccommodationsForHost_thenReturnHostsAccommodations() {
        doReturn(new GeneralResponse("host@gmail.com", "200"))
                .when(microserviceCommunicator)
                .processResponse(config.userServiceAPI() + "/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");

        List<ReservationResponseDTO> reservations = new ArrayList<>();
        doReturn(new GeneralResponse(reservations, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/reservation/1",
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
    public void whenCreateAvailabilityAndPriceInfo_thenReturnChangedAccommodation() {
        String requestBody = "{\n" +
                "    \"availabilityPeriod\": {" +
                "        \"id\": 1," +
                "        \"startDate\": 1714514400000," +
                "        \"endDate\": 1717106400000," +
                "        \"accommodationId\": 1," +
                "        \"specialAccommodationPricePeriods\": [" +
                "            {" +
                "                \"startDate\": 1714687200000," +
                "                \"endDate\": 1715119200000," +
                "                \"price\": 209.32" +
                "            }" +
                "        ]" +
                "    }," +
                "    \"price\": 121.20," +
                "    \"isPricePerGuest\": true," +
                "    \"isAvailabilityPeriodBeingUpdated\": false" +
                "}";

        doReturn(new GeneralResponse("host@gmail.com", "200"))
                .when(microserviceCommunicator)
                .processResponse(config.userServiceAPI()+ "/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");

        List<ReservationResponseDTO> reservations = new ArrayList<>();
        doReturn(new GeneralResponse(reservations, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI()+ "/reservation/1",
                        "GET",
                        "Bearer good-jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
                .body(requestBody)
        .when()
                .post(changeAvailabilityAndPriceInfoEndpoint)
        .then()
                .statusCode(200)
                .body("data.location.country", equalTo("Serbia"))
                .body("data.location.city", equalTo("Subotica"))
                .body("data.accommodationFeatures.size()", equalTo(2))
                .body("data.photographURL", equalTo(""))
                .body("data.minimumNoGuests", equalTo(1))
                .body("data.maximumNoGuests", equalTo(10))
                .body("data.hostEmail", equalTo("host@gmail.com"))
                .body("data.availabilityPeriods.size()", equalTo(1))
                .body("data.price", equalTo(121.2F))
                .body("data.pricePerGuest", equalTo(true))
                .body("data.availabilityPeriods.size()", equalTo(1))
                .body("message", equalTo("Availability period successfully added"));
    }


    @Test
    @Order(5)
    public void whenChangeAvailabilityAndPriceInfo_thenReturnChangedAccommodation() {
        String requestBody = "{\n" +
                "    \"availabilityPeriod\": {" +
                "        \"id\": 1," +
                "        \"startDate\": 1714514400000," +
                "        \"endDate\": 1717106400000," +
                "        \"accommodationId\": 1," +
                "        \"specialAccommodationPricePeriods\": [" +
                "            {" +
                "                \"startDate\": 1714687200000," +
                "                \"endDate\": 1715119200000," +
                "                \"price\": 211.32" +
                "            }," +
                "            {" +
                "                \"startDate\": 1715896800000," +
                "                \"endDate\": 1716156000000," +
                "                \"price\": 158.32" +
                "            }" +
                "        ]" +
                "    }," +
                "    \"price\": 116.20," +
                "    \"isPricePerGuest\": false," +
                "    \"isAvailabilityPeriodBeingUpdated\": true" +
                "}";

        doReturn(new GeneralResponse("host@gmail.com", "200"))
                .when(microserviceCommunicator)
                .processResponse(config.userServiceAPI()+ "/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");

        List<ReservationResponseDTO> reservations = new ArrayList<>();
        doReturn(new GeneralResponse(reservations, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/reservation/1",
                        "GET",
                        "Bearer good-jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
                .body(requestBody)
        .when()
                .post(changeAvailabilityAndPriceInfoEndpoint)
        .then()
                .statusCode(200)
                .body("data.location.country", equalTo("Serbia"))
                .body("data.location.city", equalTo("Subotica"))
                .body("data.accommodationFeatures.size()", equalTo(2))
                .body("data.photographURL", equalTo(""))
                .body("data.minimumNoGuests", equalTo(1))
                .body("data.maximumNoGuests", equalTo(10))
                .body("data.hostEmail", equalTo("host@gmail.com"))
                .body("data.availabilityPeriods.size()", equalTo(1))
                .body("data.price", equalTo(116.2F))
                .body("data.pricePerGuest", equalTo(false))
                .body("data.availabilityPeriods.size()", equalTo(1))
                .body("message", equalTo("Availability period successfully added"));
    }

    @Test
    @Order(6)
    public void whenCreateAvailabilityAndPriceInfoWithInvalidDates_thenReturnBadRequest() {
        String requestBody = "{\n" +
                "    \"availabilityPeriod\": {" +
                "        \"id\": 1," +
                "        \"startDate\": 1714514400000," +
                "        \"endDate\": 1717106400000," +
                "        \"accommodationId\": 1," +
                "        \"specialAccommodationPricePeriods\": [" +
                "            {" +
                "                \"startDate\": 1714687200000," +
                "                \"endDate\": 1715119200000," +
                "                \"price\": 211.32" +
                "            }," +
                "            {" +
                "                \"startDate\": 1715896800000," +
                "                \"endDate\": 1716156000000," +
                "                \"price\": 158.32" +
                "            }" +
                "        ]" +
                "    }," +
                "    \"price\": 116.20," +
                "    \"isPricePerGuest\": false," +
                "    \"isAvailabilityPeriodBeingUpdated\": false" +
                "}";

        doReturn(new GeneralResponse("host@gmail.com", "200"))
                .when(microserviceCommunicator)
                .processResponse(config.userServiceAPI() + "/auth/authorize/host",
                        "GET",
                        "Bearer good-jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
                .body(requestBody)
        .when()
                .post(changeAvailabilityAndPriceInfoEndpoint)
        .then()
                .statusCode(400)
                .body("data", equalTo(""))
                .body("message", equalTo("Provided availability period dates aren't valid"));
    }

    @Test
    @Order(7)
    public void whenFilterAvailabilityPeriods_thenReturnAccommodationWithAvailabilityPeriodInRange() {

        List<ReservationResponseDTO> reservations = new ArrayList<>();
        doReturn(new GeneralResponse(reservations, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/reservation/1",
                        "GET",
                        "");

        doReturn(new GeneralResponse(3.2f, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.userServiceAPI() + "/user/avg-rating/some-accommodation",
                        "GET",
                        "");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
        .when()
                .get(filterWithValidArgumentsEndpoint)
        .then()
                .statusCode(200)
                .body("data.availabilityPeriods.size()", equalTo(1))
                .body("message", equalTo("Successfully retrieved accommodations"));
    }

    @Test
    @Order(8)
    public void whenFilterAvailabilityPeriodsWithInvalidArguments_thenReturnNoAvailabilityPeriods() {
        List<ReservationResponseDTO> reservations = new ArrayList<>();
        doReturn(new GeneralResponse(reservations, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/reservation/1",
                        "GET",
                        "Bearer good-jwt");
        doReturn(new GeneralResponse(3.2f, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.userServiceAPI() + "/user/avg-rating/accommodation-name",
                        "GET",
                        "");
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
        .when()
                .get(filterWithInvalidArgumentsEndpoint)
        .then()
                .statusCode(200)
                .body("data.availabilityPeriods.size()", equalTo(0))
                .body("message", equalTo("Successfully retrieved accommodations"));
    }

    @Test
    @Order(9)
    public void whenDeactivateHost_thenTerminateAccommodations() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
        .when()
                .delete(deactivateHostsAccommodationsEndpoint)
        .then()
                .statusCode(200)
                .body("data", equalTo(true))
                .body("message", equalTo("Deactivation of hosts accommodations complete"));

    }

    @Test
    @Order(10)
    public void whenRetrieveMinAccommodations_thenReturnMinInfo() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer good-jwt")
        .when()
                .get(retrieveMinAccommodationEndpoint)
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(1))
                .body("message", equalTo("Successfully retrieved names of accommodations"));
    }

}

