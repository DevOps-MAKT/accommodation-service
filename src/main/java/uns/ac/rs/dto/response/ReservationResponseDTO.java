package uns.ac.rs.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import uns.ac.rs.model.ReservationStatus;

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
}
