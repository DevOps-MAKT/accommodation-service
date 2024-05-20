package uns.ac.rs.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.MicroserviceCommunicator;
import uns.ac.rs.dto.AdditionalAccommodationInfoDTO;
import uns.ac.rs.dto.request.AccommodationRequestDTO;
import uns.ac.rs.dto.response.AccommodationResponseDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.service.AccommodationService;
import uns.ac.rs.service.AvailabilityPeriodService;

import java.util.ArrayList;
import java.util.List;

@Path("/accommodation")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private MicroserviceCommunicator microserviceCommunicator;

    @Autowired
    private AvailabilityPeriodService availabilityPeriodService;


    @POST
    @Path("/create")
    public Response createAccommodation(@HeaderParam("Authorization") String authorizationHeader, AccommodationRequestDTO accommodationDTO) {
        // #TODO load the URL based on the env
        GeneralResponse response = microserviceCommunicator.processResponse(
                "http://localhost:8001/user-service/auth/authorize/host",
                "GET",
                authorizationHeader);

        String userEmail = (String) response.getData();
        if (userEmail.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }

        Accommodation accommodation = accommodationService.createAccommodation(accommodationDTO, userEmail);
        return Response.status(Response.Status.CREATED)
                .entity(new GeneralResponse<>(new AccommodationResponseDTO(accommodation),
                "Accommodation successfully created"))
                .build();
    }

    @GET
    @Path("/my-accommodations")
    public Response getAccommodationsFromHost(@HeaderParam("Authorization") String authorizationHeader) {
        // #TODO load the URL based on the env
        GeneralResponse response = microserviceCommunicator.processResponse(
                "http://localhost:8001/user-service/auth/authorize/host",
                "GET",
                authorizationHeader);
        String userEmail = (String) response.getData();
        if (userEmail.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }
        List<Accommodation> hostsAccommodations = accommodationService.getHostsAccommodations(userEmail);
        List<AccommodationResponseDTO> accommodationResponseDTOS = new ArrayList<>();
        for (Accommodation hostsAccommodation: hostsAccommodations) {
            accommodationResponseDTOS.add(new AccommodationResponseDTO(hostsAccommodation));
        }
        return Response
                .ok()
                .entity(new GeneralResponse<>(accommodationResponseDTOS,
                        "Host's accommodations successfully retrieved"))
                .build();
    }

    @POST
    @Path("/change-availability-and-price-info")
    public Response changeAvailabilityAndPriceInfo(@HeaderParam("Authorization") String authorizationHeader, AdditionalAccommodationInfoDTO additionalAccommodationInfoDTO) {
        // #TODO load the URL based on the env
        GeneralResponse response = microserviceCommunicator.processResponse(
                "http://localhost:8001/user-service/auth/authorize/host",
                "GET",
                authorizationHeader);

        String userEmail = (String) response.getData();
        if (userEmail.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }

        if (!additionalAccommodationInfoDTO.getIsAvailabilityPeriodBeingUpdated()){
            boolean areAvailabilityDatesValid = availabilityPeriodService.areAvailabilityPeriodDatesValid(additionalAccommodationInfoDTO.getAvailabilityPeriod());
            if (!areAvailabilityDatesValid) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new GeneralResponse<>("", "Provided availability period dates aren't valid"))
                        .build();
            }
        }

        boolean areSpecialAccommodationPriceDatesValid = availabilityPeriodService
                .areSpecialAccommodationPriceDatePeriodsValid(additionalAccommodationInfoDTO.getAvailabilityPeriod());
        if (!areSpecialAccommodationPriceDatesValid) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new GeneralResponse<>("", "Provided special price dates aren't valid"))
                    .build();
        }
        Accommodation accommodation = availabilityPeriodService.changeAvailabilityPeriodAndPriceInfo(additionalAccommodationInfoDTO);

        return Response
                .ok()
                .entity(new GeneralResponse<>(new AccommodationResponseDTO(accommodation),
                            "Availability period successfully added"))
                .build();

    }
}
