package uns.ac.rs.dto;

import lombok.Data;
import uns.ac.rs.model.SpecialAccommodationPricePeriod;

@Data
public class SpecialAccommodationPricePeriodDTO {
    private long startDate;
    private long endDate;
    private float price;

    public SpecialAccommodationPricePeriodDTO() {

    }

    public SpecialAccommodationPricePeriodDTO(SpecialAccommodationPricePeriod specialAccommodationPricePeriod) {
        this.startDate = specialAccommodationPricePeriod.getStartDate();
        this.endDate = specialAccommodationPricePeriod.getEndDate();
        this.price = specialAccommodationPricePeriod.getPrice();
    }
}
