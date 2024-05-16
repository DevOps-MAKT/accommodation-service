package uns.ac.rs.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="accommodation_features")
public class AccommodationFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feature;
}
