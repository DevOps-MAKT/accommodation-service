package uns.ac.rs.model;

import jakarta.persistence.*;
import lombok.Data;
import uns.ac.rs.dto.AvailabilityPeriodDTO;
import uns.ac.rs.dto.SpecialAccommodationPricePeriodDTO;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="availability_periods")
public class AvailabilityPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private Long startDate;

    @Column(name = "end_date")
    private Long endDate;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "availability_period_id")
    private List<SpecialAccommodationPricePeriod> specialAccommodationPricePeriods;


    public AvailabilityPeriod() {

    }

    public AvailabilityPeriod(AvailabilityPeriodDTO availabilityPeriodDTO, List<SpecialAccommodationPricePeriod> specialAccommodationPricePeriods) {
        this.startDate = availabilityPeriodDTO.getStartDate();
        this.endDate = availabilityPeriodDTO.getEndDate();
        this.specialAccommodationPricePeriods = specialAccommodationPricePeriods;
    }
}
