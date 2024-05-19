package uns.ac.rs.dto.response;

import lombok.Data;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.dto.LocationDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AccommodationFeature;
import uns.ac.rs.model.AvailabilityPeriod;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccommodationResponseDTO {
    private long id;

    private LocationDTO location;
    private List<AccommodationFeatureDTO> accommodationFeatures;
    private List<String> photographs;
    private int minimumNoGuests;
    private int maximumNoGuests;
    private String hostEmail;
    private List<AvailabilityPeriod> availabilityPeriods;


    public AccommodationResponseDTO(Accommodation accommodation) {
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
        this.availabilityPeriods = accommodation.getAvailabilityPeriods();
    }
}
