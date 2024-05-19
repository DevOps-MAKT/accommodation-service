package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.AvailabilityPeriod;

import java.util.List;

@Repository
public class AvailabilityPeriodRepository implements PanacheRepository<AvailabilityPeriod> {

    public List<AvailabilityPeriod> findByAccommodationId(long accommodationId) {
        return list("accommodation_id = ?1", accommodationId);
    }

}
