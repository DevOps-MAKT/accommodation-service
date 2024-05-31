package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class AdditionalAccommodationInfoDTO {
    private AvailabilityPeriodDTO availabilityPeriod;
    private float price;
    private boolean isPricePerGuest;
    private boolean isAvailabilityPeriodBeingUpdated;

    public boolean getIsPricePerGuest() {
        return isPricePerGuest;
    }

    public boolean getIsAvailabilityPeriodBeingUpdated() {
        return isAvailabilityPeriodBeingUpdated;
    }

    public void setIsPricePerGuest(boolean pricePerGuest) {
        isPricePerGuest = pricePerGuest;
    }

    public void setIsAvailabilityPeriodBeingUpdated(boolean availabilityPeriodBeingUpdated) {
        isAvailabilityPeriodBeingUpdated = availabilityPeriodBeingUpdated;
    }
}
