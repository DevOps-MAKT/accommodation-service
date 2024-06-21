package uns.ac.rs.model;

import jakarta.persistence.*;
import lombok.Data;
import uns.ac.rs.dto.SpecialAccommodationPricePeriodDTO;

@Entity
@Data
@Table(name="special_accommodation_price_period")
public class SpecialAccommodationPricePeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private Long startDate;

    @Column(name = "end_date")
    private Long endDate;

    @Column(name = "price")
    private float price;

    public SpecialAccommodationPricePeriod() {

    }

    public SpecialAccommodationPricePeriod(SpecialAccommodationPricePeriodDTO specialAccommodationPricePeriodDTO) {
        this.startDate = specialAccommodationPricePeriodDTO.getStartDate();
        this.endDate = specialAccommodationPricePeriodDTO.getEndDate();
        this.price = specialAccommodationPricePeriodDTO.getPrice();
    }
}
