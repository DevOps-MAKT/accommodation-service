package uns.ac.rs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import uns.ac.rs.dto.request.AccommodationRequestDTO;

import java.util.List;

@Entity
@Data
@Table(name="accommodations")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="location_id")
    private Location location;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "accommodation_accommodation_feature",
            joinColumns = @JoinColumn(name = "accommodation_id"),
            inverseJoinColumns = @JoinColumn(name = "accommodation_feature_id")
    )
    private List<AccommodationFeature> features;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "accommodation_photographs", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "photographs")
    private List<String> photographs;

    @Column(name = "minimum_no_guests")
    @Min(value = 1)
    private int minimumNoGuests;

    @Column(name = "maximum_no_guests")
    private int maximumNoGuests;

    @Column(name = "host_email")
    private String hostEmail;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "accommodation_id")
    private List<AvailabilityPeriod> availabilityPeriods;

    @Column(name = "price")
    private float price;

    @Column(name = "is_price_per_guest")
    private boolean isPricePerGuest;

    @Column(name = "terminated")
    private boolean terminated;


    public Accommodation(Location location, List<AccommodationFeature> accommodationFeatures, AccommodationRequestDTO accommodationDTO, String hostEmail) {
        this.location = location;
        this.features = accommodationFeatures;
        this.photographs = accommodationDTO.getPhotographs();
        this.minimumNoGuests = accommodationDTO.getMinimumNoGuests();
        this.maximumNoGuests = accommodationDTO.getMaximumNoGuests();
        this.hostEmail = hostEmail;
        this.terminated = false;
    }

    public Accommodation() {

    }
}
