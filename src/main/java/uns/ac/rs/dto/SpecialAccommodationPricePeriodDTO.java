package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.SpecialAccommodationPricePeriod;

@Data
@RegisterForReflection
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
