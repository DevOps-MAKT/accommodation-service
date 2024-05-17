package uns.ac.rs.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.model.AccommodationFeature;
import uns.ac.rs.service.AccommodationFeatureService;
import uns.ac.rs.service.AccommodationService;

import java.util.ArrayList;
import java.util.List;

@Path("/accommodation-features")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccommodationFeatureController {

    @Autowired
    private AccommodationFeatureService accommodationFeatureService;

    @GET
    @Path("/get")
    public Response getAccommodationFeatures() {
        List<AccommodationFeature> accommodationFeatures = accommodationFeatureService.getAccommodationFeatures();
        List<AccommodationFeatureDTO> accommodationFeatureDTOS = new ArrayList<>();
        for (AccommodationFeature accommodationFeature: accommodationFeatures) {
            accommodationFeatureDTOS.add(new AccommodationFeatureDTO(accommodationFeature));
        }
        return Response.ok()
                .entity(new GeneralResponse<>(accommodationFeatureDTOS, "Successfully retrieved accommodation features"))
                .build();
    }
}
