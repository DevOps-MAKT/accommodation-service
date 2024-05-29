package uns.ac.rs.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.dto.MinAccommodationDTO;
import uns.ac.rs.dto.request.AccommodationRequestDTO;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.dto.response.AccommodationResponseDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AccommodationFeature;
import uns.ac.rs.model.AvailabilityPeriod;
import uns.ac.rs.model.Location;
import uns.ac.rs.repository.AccommodationFeatureRepository;
import uns.ac.rs.repository.AccommodationRepository;
import uns.ac.rs.repository.LocationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private AccommodationFeatureRepository accommodationFeatureRepository;

    @Autowired
    private LocationRepository locationRepository;

    public Accommodation createAccommodation(AccommodationRequestDTO accommodationDTO, String hostEmail) {
        Location location = locationRepository.findByCityAndCountry(accommodationDTO.getLocation().getCity(), accommodationDTO.getLocation().getCountry());

        List<AccommodationFeature> accommodationFeatures = new ArrayList<>();
        for (AccommodationFeatureDTO accommodationFeatureDTO: accommodationDTO.getAccommodationFeatures()) {
            accommodationFeatures.add(accommodationFeatureRepository.findByFeature(accommodationFeatureDTO.getFeature()));
        }

        Accommodation accommodation = new Accommodation(location, accommodationFeatures, accommodationDTO, hostEmail);
        accommodationRepository.persist(accommodation);

        return accommodation;
    }

    public Optional<List<Accommodation>> getHostsAccommodations(String hostEmail) {
        return accommodationRepository.findByHostEmail(hostEmail);
    }

    public List<AccommodationResponseDTO> filter(String country, String city, int noGuests, long startDate, long endDate) {
        List<Accommodation> accommodations;
        if (areThereQueryParameters(country, noGuests, startDate, endDate)) accommodations = accommodationRepository.listAll();
        else {
            String query = "";
            query = getCountryCityQuery(country, city, query);
            query = getNoGuestsQuery(noGuests, query);
            String final_query = getFinalQuery(query);
            accommodations = accommodationRepository.filter(final_query);
        }
        return checkAvailabilityPeriods(startDate, endDate, accommodations);
    }

    public boolean deactivateHostsAccommodations(String email) {
        try {
            Optional<List<Accommodation>> accommodations = accommodationRepository.findByHostEmail(email);
            if (accommodations.isPresent()) {
                for (Accommodation accommodation: accommodations.get()) {
                    accommodation.setTerminated(true);
                }
            }
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public List<MinAccommodationDTO> retrieveMinAccommodations() {
        List<MinAccommodationDTO> minAccommodationDTOS = new ArrayList<>();
        List<Accommodation> accommodations = accommodationRepository.listAll();
        for (Accommodation accommodation: accommodations) {
            minAccommodationDTOS.add(new MinAccommodationDTO(accommodation.getId(), accommodation.getName()));
        }
        return minAccommodationDTOS;
    }

    private List<AccommodationResponseDTO> checkAvailabilityPeriods(long startDate, long endDate, List<Accommodation> accommodations) {
        List<AccommodationResponseDTO> accommodationResponseDTOS = new ArrayList<>();
        for (Accommodation accommodation: accommodations) {
            if (accommodation.getAvailabilityPeriods() != null) {
                List<AvailabilityPeriod> availabilityPeriods = accommodation.getAvailabilityPeriods();
                List<AvailabilityPeriod> acceptedAvailabilityPeriods = deepCopyAvailabilityPeriods(availabilityPeriods);
                for (AvailabilityPeriod availabilityPeriod: availabilityPeriods) {
                    if (startDate != 0 && (availabilityPeriod.getStartDate() > startDate || availabilityPeriod.getEndDate() < startDate)) {
                        acceptedAvailabilityPeriods.remove(availabilityPeriod);
                        continue;
                    }
                    if (endDate != 0 && (availabilityPeriod.getEndDate() < endDate || availabilityPeriod.getStartDate() > endDate)) {
                        acceptedAvailabilityPeriods.remove(availabilityPeriod);
                    }
                }
                AccommodationResponseDTO accommodationResponseDTO = new AccommodationResponseDTO(accommodation, acceptedAvailabilityPeriods);
                accommodationResponseDTOS.add(accommodationResponseDTO);
            }
            else {
                AccommodationResponseDTO accommodationResponseDTO = new AccommodationResponseDTO(accommodation);
                accommodationResponseDTOS.add(accommodationResponseDTO);
            }
        }
        return accommodationResponseDTOS;
    }

    private List<AvailabilityPeriod> deepCopyAvailabilityPeriods(List<AvailabilityPeriod> availabilityPeriods) {
        return new ArrayList<>(availabilityPeriods);
    }

    private boolean areThereQueryParameters(String country, int noGuests, long startDate, long endDate) {
        return country.isEmpty() && noGuests == 0 && startDate == 0 && endDate == 0;
    }

    private String getFinalQuery(String query) {
        query += "terminated = false";
        return query;
    }

    private String getNoGuestsQuery(int noGuests, String query) {
        if (noGuests != 0) {
            query += "minimumNoGuests <= " + noGuests + " and maximumNoGuests >= " + noGuests + " and ";
        }
        return query;
    }

    private String getCountryCityQuery(String country, String city, String query) {
        if (!country.isEmpty()) {
            Location location = locationRepository.findByCityAndCountry(city, country);
            query += "location.id = " + location.getId() + " and ";
        }
        return query;
    }
}
