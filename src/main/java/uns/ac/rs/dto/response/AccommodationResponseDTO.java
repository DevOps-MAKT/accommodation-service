package uns.ac.rs.dto.response;

import lombok.Data;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.dto.AvailabilityPeriodDTO;
import uns.ac.rs.dto.LocationDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AccommodationFeature;
import uns.ac.rs.model.AvailabilityPeriod;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccommodationResponseDTO {
    private long id;

    private String name;
    private LocationDTO location;
    private List<AccommodationFeatureDTO> accommodationFeatures;
    private List<String> photographs;
    private int minimumNoGuests;
    private int maximumNoGuests;
    private String hostEmail;
    private boolean isPricePerGuest;
    private float price;
    private List<AvailabilityPeriodDTO> availabilityPeriods;
    private List<ReservationResponseDTO> reservations;

    public AccommodationResponseDTO(List<ReservationResponseDTO> reservationResponseDTOS, Accommodation accommodation) {
        this.name = accommodation.getName();
        this.id = accommodation.getId();
        this.location = new LocationDTO(accommodation.getLocation());
        List<AccommodationFeatureDTO> accommodationFeatureDTOS = new ArrayList<>();
        for (AccommodationFeature accommodationFeature: accommodation.getFeatures()) {
            accommodationFeatureDTOS.add(new AccommodationFeatureDTO(accommodationFeature));
        }
        this.accommodationFeatures = accommodationFeatureDTOS;
        this.photographs = accommodation.getPhotographs();
        this.minimumNoGuests = accommodation.getMinimumNoGuests();
        this.maximumNoGuests = accommodation.getMaximumNoGuests();
        this.hostEmail = accommodation.getHostEmail();
        if (accommodation.getAvailabilityPeriods() != null) {
            List<AvailabilityPeriodDTO> availabilityPeriods = new ArrayList<>();
            for (AvailabilityPeriod availabilityPeriod: accommodation.getAvailabilityPeriods()) {
                availabilityPeriods.add(new AvailabilityPeriodDTO(availabilityPeriod, accommodation.getId()));
            }
            this.availabilityPeriods = availabilityPeriods;
        }
        this.isPricePerGuest = accommodation.isPricePerGuest();
        this.price = accommodation.getPrice();
        this.reservations = reservationResponseDTOS;
    }

    public AccommodationResponseDTO(Accommodation accommodation) {
        this.name = accommodation.getName();
        this.id = accommodation.getId();
        this.location = new LocationDTO(accommodation.getLocation());
        List<AccommodationFeatureDTO> accommodationFeatureDTOS = new ArrayList<>();
        for (AccommodationFeature accommodationFeature : accommodation.getFeatures()) {
            accommodationFeatureDTOS.add(new AccommodationFeatureDTO(accommodationFeature));
        }
        this.accommodationFeatures = accommodationFeatureDTOS;
        this.photographs = accommodation.getPhotographs();
        this.minimumNoGuests = accommodation.getMinimumNoGuests();
        this.maximumNoGuests = accommodation.getMaximumNoGuests();
        this.hostEmail = accommodation.getHostEmail();
        if (accommodation.getAvailabilityPeriods() != null) {
            List<AvailabilityPeriodDTO> availabilityPeriods = new ArrayList<>();
            for (AvailabilityPeriod availabilityPeriod : accommodation.getAvailabilityPeriods()) {
                availabilityPeriods.add(new AvailabilityPeriodDTO(availabilityPeriod, accommodation.getId()));
            }
            this.availabilityPeriods = availabilityPeriods;
        }
        this.isPricePerGuest = accommodation.isPricePerGuest();
        this.price = accommodation.getPrice();
    }

    public AccommodationResponseDTO(Accommodation accommodation, List<AvailabilityPeriod> acceptedAvailabilityPeriods) {
        this.name = accommodation.getName();
        this.id = accommodation.getId();
        this.location = new LocationDTO(accommodation.getLocation());
        List<AccommodationFeatureDTO> accommodationFeatureDTOS = new ArrayList<>();
        for (AccommodationFeature accommodationFeature: accommodation.getFeatures()) {
            accommodationFeatureDTOS.add(new AccommodationFeatureDTO(accommodationFeature));
        }
        this.accommodationFeatures = accommodationFeatureDTOS;
        this.photographs = accommodation.getPhotographs();
        this.minimumNoGuests = accommodation.getMinimumNoGuests();
        this.maximumNoGuests = accommodation.getMaximumNoGuests();
        this.hostEmail = accommodation.getHostEmail();
        List<AvailabilityPeriodDTO> availabilityPeriods = new ArrayList<>();
        for (AvailabilityPeriod availabilityPeriod: acceptedAvailabilityPeriods) {
            availabilityPeriods.add(new AvailabilityPeriodDTO(availabilityPeriod, accommodation.getId()));
        }
        this.availabilityPeriods = availabilityPeriods;
        this.isPricePerGuest = accommodation.isPricePerGuest();
        this.price = accommodation.getPrice();
    }
}
