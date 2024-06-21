package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class AdditionalAccommodationInfoDTO {
    private AvailabilityPeriodDTO availabilityPeriod;
    private boolean isAvailabilityPeriodBeingUpdated;

    public boolean getIsAvailabilityPeriodBeingUpdated() {
        return isAvailabilityPeriodBeingUpdated;
    }

    public void setIsAvailabilityPeriodBeingUpdated(boolean availabilityPeriodBeingUpdated) {
        isAvailabilityPeriodBeingUpdated = availabilityPeriodBeingUpdated;
    }
}
