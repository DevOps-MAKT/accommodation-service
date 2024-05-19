package uns.ac.rs.dto;

import lombok.Data;

import java.util.List;

@Data
public class AvailabilityPeriodDTO {

    private long id;
    private long startDate;
    private long endDate;
    private long accommodationId;
    private List<SpecialAccommodationPricePeriodDTO> specialAccommodationPricePeriods;
}
