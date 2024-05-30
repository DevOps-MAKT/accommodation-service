package uns.ac.rs.dto.request;

import lombok.Data;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.dto.LocationDTO;

import java.util.List;

@Data
public class AccommodationRequestDTO {

    private String name;
    private LocationDTO location;
    private List<AccommodationFeatureDTO> accommodationFeatures;
    private int minimumNoGuests;
    private int maximumNoGuests;

    public AccommodationRequestDTO() {

    }

}
