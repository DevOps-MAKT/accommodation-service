package uns.ac.rs.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class PriceInfoDTO {

    private long accommodationId;
    private float price;
    private boolean pricePerGuest;

    public PriceInfoDTO() {

    }
}
