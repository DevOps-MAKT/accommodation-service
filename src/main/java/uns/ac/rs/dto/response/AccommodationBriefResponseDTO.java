package uns.ac.rs.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.dto.LocationDTO;

import java.util.List;

@Data
@RegisterForReflection
public class AccommodationBriefResponseDTO {
    private long id;
    private String name;
    private LocationDTO location;
    private List<AccommodationFeatureDTO> accommodationFeatures;
    private String photographURL;
    private int minimumNoGuests;
    private int maximumNoGuests;
    private String hostEmail;
    private boolean isPricePerGuest;
    private float price;
    private float avgRating;

    public AccommodationBriefResponseDTO(AccommodationResponseDTO accommodation) {
        this.name = accommodation.getName();
        this.id = accommodation.getId();
        this.location = accommodation.getLocation();
        this.accommodationFeatures = accommodation.getAccommodationFeatures();
        this.photographURL = accommodation.getPhotographURL();
        this.minimumNoGuests = accommodation.getMinimumNoGuests();
        this.maximumNoGuests = accommodation.getMaximumNoGuests();
        this.hostEmail = accommodation.getHostEmail();
        this.isPricePerGuest = accommodation.isPricePerGuest();
        this.price = accommodation.getPrice();
    }
}
