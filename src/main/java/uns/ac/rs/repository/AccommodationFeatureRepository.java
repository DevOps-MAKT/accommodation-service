package uns.ac.rs.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.springframework.stereotype.Repository;
import uns.ac.rs.model.AccommodationFeature;

@Repository
public class AccommodationFeatureRepository implements PanacheRepository<AccommodationFeature> {

    public AccommodationFeature findByFeature(String feature) {
        return find("feature = ?1", feature).firstResult();
    }

}
