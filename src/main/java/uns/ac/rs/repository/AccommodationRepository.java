package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.Accommodation;

import java.util.List;
import java.util.Optional;

@Repository
public class AccommodationRepository implements PanacheRepository<Accommodation> {
    public Optional<List<Accommodation>> findByHostEmail(String hostEmail) {
        return Optional.ofNullable(list("hostEmail", hostEmail));
    }

    public Accommodation findById(Long id) {
        return find("id = ?1 and terminated = ?2", id, false).firstResult();
    }

    public List<Accommodation> filter(String query) {
        return list(query);
    }
}
