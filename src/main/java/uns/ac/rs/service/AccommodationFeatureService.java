package uns.ac.rs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.model.AccommodationFeature;
import uns.ac.rs.repository.AccommodationFeatureRepository;

import java.util.List;

@Service
public class AccommodationFeatureService {

    @Autowired
    public AccommodationFeatureRepository accommodationFeatureRepository;

    public List<AccommodationFeature> getAccommodationFeatures() {
        return accommodationFeatureRepository.listAll();
    }
}
