package uns.ac.rs.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.dto.LocationDTO;
import uns.ac.rs.dto.request.AccommodationRequestDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AccommodationFeature;
import uns.ac.rs.model.Location;
import uns.ac.rs.repository.AccommodationFeatureRepository;
import uns.ac.rs.repository.AccommodationRepository;
import uns.ac.rs.repository.LocationRepository;
import uns.ac.rs.service.AccommodationService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTests {

    @InjectMocks
    private AccommodationService accommodationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private AccommodationFeatureRepository accommodationFeatureRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Test
    public void testCreateAccommodation() {
        AccommodationRequestDTO accommodationDTO = new AccommodationRequestDTO();
        accommodationDTO.setLocation(new LocationDTO("Serbia", "Subotica"));
        List<AccommodationFeatureDTO> accommodationFeaturesDTO = new ArrayList<>();
        accommodationFeaturesDTO.add(new AccommodationFeatureDTO("Feature1"));
        accommodationFeaturesDTO.add(new AccommodationFeatureDTO("Feature2"));
        accommodationDTO.setAccommodationFeatures(accommodationFeaturesDTO);
        String userEmail = "test@example.com";

        Location mockLocation = new Location("Subotica", "Serbia");
        when(locationRepository.findByCityAndCountry("Subotica", "Serbia")).thenReturn(mockLocation);

        AccommodationFeature mockFeature1 = new AccommodationFeature("Feature1");
        AccommodationFeature mockFeature2 = new AccommodationFeature("Feature2");
        when(accommodationFeatureRepository.findByFeature("Feature1")).thenReturn(mockFeature1);
        when(accommodationFeatureRepository.findByFeature("Feature2")).thenReturn(mockFeature2);

        Accommodation result = accommodationService.createAccommodation(accommodationDTO, userEmail);

        assertEquals("Subotica", result.getLocation().getCity());
        assertEquals("Serbia", result.getLocation().getCountry());
        assertEquals(2, result.getFeatures().size());
        assertEquals("test@example.com", result.getHostEmail());

        verify(locationRepository, times(1)).findByCityAndCountry("Subotica", "Serbia");
        verify(accommodationFeatureRepository, times(2)).findByFeature(anyString());
        verify(accommodationRepository, times(1)).persist(result);
    }
}
