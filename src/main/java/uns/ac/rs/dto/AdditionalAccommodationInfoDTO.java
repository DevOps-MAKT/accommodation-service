package uns.ac.rs.dto;

import lombok.Data;

@Data
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
