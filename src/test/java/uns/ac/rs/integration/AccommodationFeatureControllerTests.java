package uns.ac.rs.integration;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import uns.ac.rs.controller.AccommodationFeatureController;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class AccommodationFeatureControllerTests {
    @TestHTTPEndpoint(AccommodationFeatureController.class)
    @TestHTTPResource("get")
    URL getAccommodationFeaturesEndpoint;

    @Test
    public void whenGetAccommodationFeatures_thenReturnOkWithAccommodationFeatures() {

        given()
                .contentType(ContentType.JSON)
        .when()
                .get(getAccommodationFeaturesEndpoint)
        .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("message", equalTo("Successfully retrieved accommodation features"));
    }
}
