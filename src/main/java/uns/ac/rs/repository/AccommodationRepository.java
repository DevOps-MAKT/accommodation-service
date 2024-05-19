package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.Location;

import java.util.List;

@Repository
public class AccommodationRepository implements PanacheRepository<Accommodation> {
    public List<Accommodation> findByHostEmail(String hostEmail) {
        return list("hostEmail", hostEmail);
    }

    public Accommodation findById(Long id) {
        return find("id = ?1", id).firstResult();
    }
}
