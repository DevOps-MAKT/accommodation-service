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
import org.jboss.resteasy.reactive.MultipartForm;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.MicroserviceCommunicator;
import uns.ac.rs.config.IntegrationConfig;
import uns.ac.rs.dto.AdditionalAccommodationInfoDTO;
import uns.ac.rs.dto.MinAccommodationDTO;
import uns.ac.rs.dto.PriceInfoDTO;
import uns.ac.rs.dto.request.AccommodationForm;
import uns.ac.rs.dto.response.AccommodationBriefResponseDTO;
import uns.ac.rs.dto.response.AccommodationResponseDTO;
import uns.ac.rs.dto.response.ReservationResponseDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.service.AccommodationService;
import uns.ac.rs.service.AvailabilityPeriodService;
import uns.ac.rs.service.PhotographService;

import java.io.InputStream;
import java.util.*;

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


    @Autowired
    private PhotographService photographService;


    @POST
    @Path("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createAccommodation(@HeaderParam("Authorization") String authorizationHeader, @MultipartForm AccommodationForm form) {
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/auth/authorize/host",
                "GET",
                authorizationHeader);

        String userEmail = (String) response.getData();
        if (userEmail.equals("")) {
            logger.warn("Unauthorized access for create accommodation");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }

        try (InputStream input = form.file) {
            logger.info("Saving provided photograph");
            String imageFileName = photographService.save(input, form.fileName);
            if (Objects.equals(imageFileName, "")) {
                logger.warn("Photograph has not been saved successfully");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new GeneralResponse<>("", "Error while saving the picture")).build();
            }

            logger.info("Creating accommodation");
            Accommodation accommodation = accommodationService.createAccommodation(form, userEmail, imageFileName);
            List<ReservationResponseDTO> reservationResponseDTOS = new ArrayList<>();
            logger.info("Accommodation successfully created");
            return Response.status(Response.Status.CREATED)
                    .entity(new GeneralResponse<>(new AccommodationResponseDTO(reservationResponseDTOS, accommodation),
                    "Accommodation successfully created"))
                    .build();
        } catch (Exception e) {
            logger.error("Unsuccessful accommodation creation: {}", e.toString());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating accommodation").build();
        }
    }

    @GET
    @Path("/my-accommodations")
    public Response getAccommodationsFromHost(@HeaderParam("Authorization") String authorizationHeader) {
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/auth/authorize/host",
                HttpMethod.GET,
                authorizationHeader);
        String userEmail = (String) response.getData();
        logger.info("Retrieving accommodations for host with email {}", userEmail);
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
    @Path("/change-availability-info")
    public Response changeAvailabilityAndPriceInfo(@HeaderParam("Authorization") String authorizationHeader, AdditionalAccommodationInfoDTO additionalAccommodationInfoDTO) {
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/auth/authorize/host",
                "GET",
                authorizationHeader);

        String userEmail = (String) response.getData();
        if (userEmail.equals("")) {
            logger.warn("Unauthorized access for host's accommodation");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }

        logger.info("Checking whether availability period dates are valid");
        if (!additionalAccommodationInfoDTO.getIsAvailabilityPeriodBeingUpdated()){
            boolean areAvailabilityDatesValid = availabilityPeriodService.areAvailabilityPeriodDatesValid(additionalAccommodationInfoDTO.getAvailabilityPeriod());
            if (!areAvailabilityDatesValid) {
                logger.warn("Provided availability period dates are not valid");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new GeneralResponse<>("", "Provided availability period dates aren't valid"))
                        .build();
            }
        }

        long accommodationId = additionalAccommodationInfoDTO.getAvailabilityPeriod().getAccommodationId();
        logger.info("Retrieving reservations for provided accommodation");
        GeneralResponse reservationsResponse = microserviceCommunicator.processResponse(
                config.reservationServiceAPI() + "/reservation/" + accommodationId,
                "GET",
                authorizationHeader);

        List<LinkedHashMap> reservationsHashMap = (List<LinkedHashMap>) reservationsResponse.getData();
        List<ReservationResponseDTO> reservations = new ArrayList<>();
        for (LinkedHashMap reservation: reservationsHashMap) {
            reservations.add(new ReservationResponseDTO(reservation));
        }
        logger.info("Successfully retrieved reservations for provided accommodation");
        if (additionalAccommodationInfoDTO.getIsAvailabilityPeriodBeingUpdated()) {
            boolean areSpecialAccommodationPriceDatesValid = availabilityPeriodService
                    .areSpecialAccommodationPriceDatePeriodsValid(additionalAccommodationInfoDTO.getAvailabilityPeriod());
            if (!areSpecialAccommodationPriceDatesValid) {
                logger.warn("Provided special accommodation price dates are not valid");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new GeneralResponse<>("", "Provided special price dates aren't valid"))
                        .build();
            }

            boolean areTherePresentReservations = availabilityPeriodService
                    .checkForReservations(additionalAccommodationInfoDTO.getAvailabilityPeriod(), reservations);
            if (areTherePresentReservations) {
                logger.warn("There are present reservations in the given date period");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new GeneralResponse<>("", "Can't change availability period dates as there are present reservations"))
                        .build();
            }
        }

        logger.info("Changing the availability period");
        Accommodation accommodation = availabilityPeriodService.changeAvailabilityPeriod(additionalAccommodationInfoDTO);
        logger.info("Successfully changed the availability period");

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

        logger.info("Filtering accommodations");
        List<AccommodationResponseDTO> accommodationResponseDTOS = accommodationService.filter(country, city, noGuests, startDate, endDate);
        logger.info("Initial filtering performed");

        logger.info("Retrieving reservations and average rating for accommodations");
        for (AccommodationResponseDTO accommodation: accommodationResponseDTOS) {
            GeneralResponse reservationsResponse = microserviceCommunicator.processResponse(
                    config.reservationServiceAPI() + "/reservation/" + accommodation.getId(),
                    "GET",
                    "");

            List<ReservationResponseDTO> reservations = (List<ReservationResponseDTO>) reservationsResponse.getData();
            accommodation.setReservations(reservations);

            GeneralResponse ratingResponse = microserviceCommunicator.processResponse(
                    config.userServiceAPI() + "/user/avg-rating/" + accommodation.getId(),
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
        logger.info("Deactivating accommodations for user with email {}", email);
        boolean successfulAccommodationDeactivation = accommodationService.deactivateHostsAccommodations(email);
        logger.info("Successfully deactivated accommodations for user with email {}", email);
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
        logger.info("Retrieving basic accommodation info");
        List<MinAccommodationDTO> minAccommodationDTOS = accommodationService.retrieveMinAccommodations();
        logger.info("Successfully retrieved basic accommodation info");
        return Response
                .ok()
                .entity(new GeneralResponse<>(minAccommodationDTOS, "Successfully retrieved names of accommodations"))
                .build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getAccommodation(@PathParam("id") Long id) {
        logger.info("Retrieving accommodation info");
        AccommodationResponseDTO accommodation = accommodationService.getById(id);

        logger.info("Retrieving reservations and average rating for accommodation");
        GeneralResponse reservationsResponse = microserviceCommunicator.processResponse(
                config.reservationServiceAPI() + "/reservation/" + accommodation.getId(),
                "GET",
                "");

        List<ReservationResponseDTO> reservations = (List<ReservationResponseDTO>) reservationsResponse.getData();
        accommodation.setReservations(reservations);

        GeneralResponse ratingResponse = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/user/avg-rating/" + accommodation.getId(),
                "GET",
                ""
        );

        Double doubleRating = (Double) ratingResponse.getData();
        float avgRating = doubleRating.floatValue();
        accommodation.setAvgRating(avgRating);

        return Response
                .ok()
                .entity(new GeneralResponse<>(accommodation, "Successfully retrieved accommodation"))
                .build();
    }

    @GET
    @Path("/brief/{id}")
    @PermitAll
    public Response getBriefAccommodation(@PathParam("id") Long id) {
        logger.info("Retrieving accommodation info");
        AccommodationResponseDTO accommodation = accommodationService.getById(id);

        GeneralResponse ratingResponse = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/user/avg-rating/" + accommodation.getId(),
                "GET",
                ""
        );

        Double doubleRating = (Double) ratingResponse.getData();
        float avgRating = doubleRating.floatValue();
        accommodation.setAvgRating(avgRating);

        return Response
                .ok()
                .entity(new GeneralResponse<>(new AccommodationBriefResponseDTO(accommodation), "Successfully retrieved accommodation"))
                .build();
    }

    @PATCH
    @Path("/update-price-info")
    @RolesAllowed("host")
    public Response updatePriceInfo(@HeaderParam("Authorization") String authorizationHeader, PriceInfoDTO priceInfoDTO) {
        GeneralResponse response = microserviceCommunicator.processResponse(
                config.userServiceAPI() + "/auth/authorize/host",
                "GET",
                authorizationHeader);

        String userEmail = (String) response.getData();
        if (userEmail.equals("")) {
            logger.warn("Unauthorized access for host's accommodation");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }
        try {
            Accommodation accommodation = accommodationService.updatePriceInfo(priceInfoDTO);
            return Response
                    .ok()
                    .entity(new GeneralResponse<>(new AccommodationResponseDTO(accommodation),
                            "Successfully updated accommodation price info"))
                    .build();
        } catch (Exception e) {
            logger.error("Error while updating price info: {}", e.getLocalizedMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating price info").build();
        }
    }
}
