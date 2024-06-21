package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.AccommodationFeature;

@Data
@RegisterForReflection
public class AccommodationFeatureDTO {

    private String feature;

    public AccommodationFeatureDTO() {

    }

    public AccommodationFeatureDTO(AccommodationFeature accommodationFeature) {
        this.feature = accommodationFeature.getFeature();
    }

    public AccommodationFeatureDTO(String feature) {
        this.feature = feature;
    }
}
