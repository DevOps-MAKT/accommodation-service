package uns.ac.rs.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.ReservationStatus;

import java.util.LinkedHashMap;

@Data
@RegisterForReflection
public class ReservationResponseDTO {

    private long id;
    private long accommodationId;
    private String hostEmail;
    private String guestEmail;
    private long startDate;
    private long endDate;
    private int noGuests;
    private ReservationStatus status;
    private int noCancellations;

    public ReservationResponseDTO() {

    }

    public ReservationResponseDTO(LinkedHashMap data) {
        this.id = ((Number) data.get("id")).longValue();
        this.accommodationId = ((Number) data.get("accommodationId")).longValue();
        this.hostEmail = (String) data.get("hostEmail");
        this.guestEmail = (String) data.get("guestEmail");
        this.startDate = (long) data.get("startDate");
        this.endDate = (long) data.get("endDate");
        this.noGuests = (int) data.get("noGuests");
        this.status = ReservationStatus.valueOf((String) data.get("status"));
        this.noCancellations = (int) data.get("noCancellations");
    }
}
