package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.Location;

@Data
@RegisterForReflection
public class LocationDTO {
    private String country;
    private String city;

    public LocationDTO(Location location) {
        country = location.getCountry();
        city = location.getCity();
    }

    public LocationDTO(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public LocationDTO() {

    }
}
