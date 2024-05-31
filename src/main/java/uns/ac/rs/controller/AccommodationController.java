package uns.ac.rs.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.MicroserviceCommunicator;
import uns.ac.rs.config.IntegrationConfig;
import uns.ac.rs.dto.AdditionalAccommodationInfoDTO;
import uns.ac.rs.dto.MinAccommodationDTO;
import uns.ac.rs.dto.request.AccommodationRequestDTO;
import uns.ac.rs.dto.response.AccommodationResponseDTO;
import uns.ac.rs.dto.response.ReservationResponseDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.service.AccommodationService;
import uns.ac.rs.service.AvailabilityPeriodService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/accommodation")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccommodationController {
    private static final Logger logger = LoggerFactory.getLogger(AccommodationController.class);
    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private MicroserviceCommunicator microserviceCommunicator;

    @Autowired
    private AvailabilityPeriodService availabilityPeriodService;

    @Inject
    private IntegrationConfig config;

    @POST
    @Path("/create")
    public Response createAccommodation(@HeaderParam("Authorization") String authorizationHeader, AccommodationRequestDTO accommodationDTO) {
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/auth/authorize/host",
                "GET",
                authorizationHeader);

        String userEmail = (String) response.getData();
        if (userEmail.equals("")) {
            logger.warn("Unauthorized access for create accommodation");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }

        Accommodation accommodation = accommodationService.createAccommodation(accommodationDTO, userEmail);
        List<ReservationResponseDTO> reservationResponseDTOS = new ArrayList<>();

        logger.info("Accommodation successfully created");
        return Response.status(Response.Status.CREATED)
                .entity(new GeneralResponse<>(new AccommodationResponseDTO(reservationResponseDTOS, accommodation),
                "Accommodation successfully created"))
                .build();
    }

    @GET
    @Path("/my-accommodations")
    public Response getAccommodationsFromHost(@HeaderParam("Authorization") String authorizationHeader) {
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/auth/authorize/host",
                HttpMethod.GET,
                authorizationHeader);
        String userEmail = (String) response.getData();
        if (userEmail.equals("")) {
            logger.warn("Unauthorized access for host's accommodation");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }

        Optional<List<Accommodation>> hostsAccommodations = accommodationService.getHostsAccommodations(userEmail);
        List<AccommodationResponseDTO> accommodationResponseDTOS = new ArrayList<>();
        if (hostsAccommodations.isPresent()) {
            for (Accommodation hostsAccommodation: hostsAccommodations.get()) {
                GeneralResponse reservationsResponse = microserviceCommunicator.processResponse(
                        config.reservationServiceAPI() + "/reservation/" + hostsAccommodation.getId(),
                        "GET",
                        authorizationHeader);
                List<ReservationResponseDTO> reservations = (List<ReservationResponseDTO>) reservationsResponse.getData();
                accommodationResponseDTOS.add(new AccommodationResponseDTO(reservations, hostsAccommodation));
            }
        }
        logger.info("Host's accommodations successfully retrieved");
        return Response
                .ok()
                .entity(new GeneralResponse<>(accommodationResponseDTOS, "Host's accommodations successfully retrieved"))
                .build();
    }

    @POST
    @Path("/change-availability-and-price-info")
    public Response changeAvailabilityAndPriceInfo(@HeaderParam("Authorization") String authorizationHeader, AdditionalAccommodationInfoDTO additionalAccommodationInfoDTO) {
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/auth/authorize/host",
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

        long accommodationId = additionalAccommodationInfoDTO.getAvailabilityPeriod().getAccommodationId();
        GeneralResponse reservationsResponse = microserviceCommunicator.processResponse(
                config.reservationServiceAPI() + "/reservation/" + accommodationId,
                "GET",
                authorizationHeader);

        List<ReservationResponseDTO> reservations = (List<ReservationResponseDTO>) reservationsResponse.getData();

        if (additionalAccommodationInfoDTO.getIsAvailabilityPeriodBeingUpdated()) {
            boolean areSpecialAccommodationPriceDatesValid = availabilityPeriodService
                    .areSpecialAccommodationPriceDatePeriodsValid(additionalAccommodationInfoDTO.getAvailabilityPeriod());
            if (!areSpecialAccommodationPriceDatesValid) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new GeneralResponse<>("", "Provided special price dates aren't valid"))
                        .build();
            }

            boolean areTherePresentReservations = availabilityPeriodService
                    .checkForReservations(additionalAccommodationInfoDTO.getAvailabilityPeriod(), reservations);
            if (areTherePresentReservations) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new GeneralResponse<>("", "Can't change availability period dates as there are present reservations"))
                        .build();
            }
        }

        Accommodation accommodation = availabilityPeriodService.changeAvailabilityPeriodAndPriceInfo(additionalAccommodationInfoDTO);

        return Response
                .ok()
                .entity(new GeneralResponse<>(new AccommodationResponseDTO(reservations, accommodation),
                            "Availability period successfully added"))
                .build();

    }

    @GET
    @Path("/filter")
    @PermitAll
    public Response filter(@QueryParam("country") String country,
                           @QueryParam("city") String city,
                           @QueryParam("noGuests") int noGuests,
                           @QueryParam("startDate") long startDate,
                           @QueryParam("endDate") long endDate) {

        List<AccommodationResponseDTO> accommodationResponseDTOS = accommodationService.filter(country, city, noGuests, startDate, endDate);
        for (AccommodationResponseDTO accommodation: accommodationResponseDTOS) {
            GeneralResponse reservationsResponse = microserviceCommunicator.processResponse(
                    config.reservationServiceAPI() + "/reservation/" + accommodation.getId(),
                    "GET",
                    "");

            List<ReservationResponseDTO> reservations = (List<ReservationResponseDTO>) reservationsResponse.getData();
            accommodation.setReservations(reservations);

            GeneralResponse ratingResponse = microserviceCommunicator.processResponse(
                    config.userServiceAPI() + "/avg-rating/" + accommodation.getName(),
                    "GET",
                    ""
            );

            float avgRating = (float) ratingResponse.getData();
            accommodation.setAvgRating(avgRating);
        }
        return Response
                .ok()
                .entity(new GeneralResponse<>(accommodationResponseDTOS, "Successfully retrieved accommodations"))
                .build();
    }

    @DELETE
    @Path("/deactivate-hosts-accommodations/{email}")
    @RolesAllowed("host")
    public Response deactivateHostsAccommodations(@PathParam("email") String email,
                                                  @HeaderParam("Authorization") String authorizationHeader) {
        boolean successfulAccommodationDeactivation = accommodationService.deactivateHostsAccommodations(email);
        return Response
                .ok()
                .entity(new GeneralResponse<>(successfulAccommodationDeactivation,
                        "Deactivation of hosts accommodations complete")
                )
                .build();
    }

    @GET
    @Path("/retrieve-min-accommodations")
    @PermitAll
    public Response retrieveAccommodations() {
        List<MinAccommodationDTO> minAccommodationDTOS = accommodationService.retrieveMinAccommodations();
        return Response
                .ok()
                .entity(new GeneralResponse<>(minAccommodationDTOS, "Successfully retrieved names of accommodations"))
                .build();
    }
}
