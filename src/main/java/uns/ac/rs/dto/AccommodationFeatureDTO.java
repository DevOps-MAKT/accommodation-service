package uns.ac.rs.dto;

import lombok.Data;
import uns.ac.rs.model.AccommodationFeature;

@Data
public class AccommodationFeatureDTO {

    private String feature;

    public AccommodationFeatureDTO() {

    }

    public AccommodationFeatureDTO(AccommodationFeature accommodationFeature) {
        this.feature = accommodationFeature.getFeature();
    }
}
