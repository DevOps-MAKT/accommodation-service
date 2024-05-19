package uns.ac.rs.dto;

import lombok.Data;

@Data
public class SpecialAccommodationPricePeriodDTO {
    private long startDate;
    private long endDate;
    private float price;
}
