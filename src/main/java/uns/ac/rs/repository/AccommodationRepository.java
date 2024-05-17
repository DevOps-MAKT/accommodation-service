package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.Accommodation;

@Repository
public class AccommodationRepository implements PanacheRepository<Accommodation> {

}
