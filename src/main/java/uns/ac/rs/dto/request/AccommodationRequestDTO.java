package uns.ac.rs.dto.request;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.dto.LocationDTO;

import java.util.List;

@Data
@RegisterForReflection
public class AccommodationRequestDTO {

    private String name;
    private LocationDTO location;
    private List<AccommodationFeatureDTO> accommodationFeatures;
    private int minimumNoGuests;
    private int maximumNoGuests;

    public AccommodationRequestDTO() {

    }

}
