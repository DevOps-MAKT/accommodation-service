package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.AvailabilityPeriod;
import uns.ac.rs.model.SpecialAccommodationPricePeriod;

import java.util.ArrayList;
import java.util.List;

@Data
@RegisterForReflection
public class AvailabilityPeriodDTO {

    private long id;
    private long startDate;
    private long endDate;
    private long accommodationId;
    private List<SpecialAccommodationPricePeriodDTO> specialAccommodationPricePeriods;

    public AvailabilityPeriodDTO() {

    }

    public AvailabilityPeriodDTO(AvailabilityPeriod availabilityPeriod, long id) {
        this.id = availabilityPeriod.getId();
        this.startDate = availabilityPeriod.getStartDate();
        this.endDate = availabilityPeriod.getEndDate();
        this.accommodationId = id;
        if (availabilityPeriod.getSpecialAccommodationPricePeriods() != null) {
            List<SpecialAccommodationPricePeriodDTO> specialAccommodationPricePeriods = new ArrayList<>();
            for (SpecialAccommodationPricePeriod specialAccommodationPricePeriod: availabilityPeriod.getSpecialAccommodationPricePeriods()) {
                specialAccommodationPricePeriods.add(new SpecialAccommodationPricePeriodDTO(specialAccommodationPricePeriod));
            }
            this.specialAccommodationPricePeriods = specialAccommodationPricePeriods;
        }

    }
}
