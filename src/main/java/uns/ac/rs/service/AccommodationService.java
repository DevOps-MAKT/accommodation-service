package uns.ac.rs.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.dto.request.AccommodationRequestDTO;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AccommodationFeature;
import uns.ac.rs.model.Location;
import uns.ac.rs.repository.AccommodationFeatureRepository;
import uns.ac.rs.repository.AccommodationRepository;
import uns.ac.rs.repository.LocationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private AccommodationFeatureRepository accommodationFeatureRepository;

    @Autowired
    private LocationRepository locationRepository;

    public Accommodation createAccommodation(AccommodationRequestDTO accommodationDTO, String userEmail) {
        Location location = locationRepository.findByCityAndCountry(accommodationDTO.getLocation().getCity(), accommodationDTO.getLocation().getCountry());

        List<AccommodationFeature> accommodationFeatures = new ArrayList<>();
        for (AccommodationFeatureDTO accommodationFeatureDTO: accommodationDTO.getAccommodationFeatures()) {
            accommodationFeatures.add(accommodationFeatureRepository.findByFeature(accommodationFeatureDTO.getFeature()));
        }

        Accommodation accommodation = new Accommodation(location, accommodationFeatures, accommodationDTO, userEmail);
        accommodationRepository.persist(accommodation);

        return accommodation;
    }
}
