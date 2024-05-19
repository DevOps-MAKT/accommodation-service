package uns.ac.rs.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uns.ac.rs.dto.AccommodationFeatureDTO;
import uns.ac.rs.dto.AvailabilityPeriodDTO;
import uns.ac.rs.dto.LocationDTO;
import uns.ac.rs.dto.request.AccommodationRequestDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AccommodationFeature;
import uns.ac.rs.model.AvailabilityPeriod;
import uns.ac.rs.model.Location;
import uns.ac.rs.repository.AccommodationFeatureRepository;
import uns.ac.rs.repository.AccommodationRepository;
import uns.ac.rs.repository.AvailabilityPeriodRepository;
import uns.ac.rs.repository.LocationRepository;
import uns.ac.rs.service.AccommodationService;
import uns.ac.rs.service.AvailabilityPeriodService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTests {

    @InjectMocks
    private AccommodationService accommodationService;

    @InjectMocks
    private AvailabilityPeriodService availabilityPeriodService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private AccommodationFeatureRepository accommodationFeatureRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AvailabilityPeriodRepository availabilityPeriodRepository;

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

    /*
    @Test
    public void testCreateAvailabilityPeriod() {

        AvailabilityPeriodDTO availabilityPeriodDTO = new AvailabilityPeriodDTO();
        availabilityPeriodDTO.setStartDate(1716156000000L);
        availabilityPeriodDTO.setEndDate(1716328800000L);
        availabilityPeriodDTO.setAccommodationId(1L);

        Accommodation accommodation = new Accommodation();
        List<AvailabilityPeriod> availabilityPeriods = new ArrayList<>();
        accommodation.setAvailabilityPeriods(availabilityPeriods);
        when(accommodationRepository.findById(availabilityPeriodDTO.getAccommodationId())).thenReturn(accommodation);

        Accommodation result = availabilityPeriodService.createAvailabilityPeriod(availabilityPeriodDTO);

        assertEquals(result.getAvailabilityPeriods().size(), 1);
        verify(availabilityPeriodRepository).persist(any(AvailabilityPeriod.class));
        verify(accommodationRepository).findById(availabilityPeriodDTO.getAccommodationId());
        verify(accommodationRepository).persist(any(Accommodation.class));
    }
     */

    @Test
    void testAreAvailabilityPeriodDatesValid_NoOverlappingPeriods() {

        AvailabilityPeriodDTO availabilityPeriodDTO = new AvailabilityPeriodDTO();
        availabilityPeriodDTO.setStartDate(1000000000L);
        availabilityPeriodDTO.setEndDate(2000000000L);
        availabilityPeriodDTO.setAccommodationId(1L);

        Accommodation accommodation = new Accommodation();
        accommodation.setAvailabilityPeriods(new ArrayList<>());

        when(accommodationRepository.findById(availabilityPeriodDTO.getAccommodationId())).thenReturn(accommodation);

        boolean result = availabilityPeriodService.areAvailabilityPeriodDatesValid(availabilityPeriodDTO);

        assertTrue(result);
    }

    @Test
    void testAreAvailabilityPeriodDatesValid_OverlappingPeriods() {

        AvailabilityPeriodDTO availabilityPeriodDTO = new AvailabilityPeriodDTO();
        availabilityPeriodDTO.setStartDate(1000000000L);
        availabilityPeriodDTO.setEndDate(2000000000L);
        availabilityPeriodDTO.setAccommodationId(1L);

        Accommodation accommodation = new Accommodation();
        AvailabilityPeriod overlappingPeriod = new AvailabilityPeriod();
        overlappingPeriod.setStartDate(1500000000L);
        overlappingPeriod.setEndDate(2500000000L);
        accommodation.setAvailabilityPeriods(Collections.singletonList(overlappingPeriod));

        when(accommodationRepository.findById(availabilityPeriodDTO.getAccommodationId())).thenReturn(accommodation);

        // Execute
        boolean result = availabilityPeriodService.areAvailabilityPeriodDatesValid(availabilityPeriodDTO);

        // Verify
        assertFalse(result);
    }
}
