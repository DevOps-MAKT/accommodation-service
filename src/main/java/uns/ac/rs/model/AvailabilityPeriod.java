package uns.ac.rs.model;

import jakarta.persistence.*;
import lombok.Data;
import uns.ac.rs.dto.AvailabilityPeriodDTO;

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


    public AvailabilityPeriod() {

    }

    public AvailabilityPeriod(AvailabilityPeriodDTO availabilityPeriodDTO) {
        this.startDate = availabilityPeriodDTO.getStartDate();
        this.endDate = availabilityPeriodDTO.getEndDate();
    }
}
