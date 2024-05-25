package uns.ac.rs.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uns.ac.rs.dto.*;
import uns.ac.rs.dto.request.AccommodationRequestDTO;
import uns.ac.rs.dto.response.AccommodationResponseDTO;
import uns.ac.rs.model.*;
import uns.ac.rs.repository.AccommodationFeatureRepository;
import uns.ac.rs.repository.AccommodationRepository;
import uns.ac.rs.repository.AvailabilityPeriodRepository;
import uns.ac.rs.repository.LocationRepository;
import uns.ac.rs.service.AccommodationService;
import uns.ac.rs.service.AvailabilityPeriodService;

import java.util.ArrayList;
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

    @Test
    public void testRetrieveAll() {
        AccommodationFeature accommodationFeature = new AccommodationFeature("AC");
        List<AccommodationFeature> accommodationFeatures = new ArrayList<>();
        accommodationFeatures.add(accommodationFeature);
        List<String> photographs = new ArrayList<>();
        photographs.add("some-picture");
        AccommodationRequestDTO accommodationRequestDTO = new AccommodationRequestDTO();
        accommodationRequestDTO.setPhotographs(photographs);
        accommodationRequestDTO.setMaximumNoGuests(5);
        accommodationRequestDTO.setMinimumNoGuests(2);
        Accommodation accommodation1 = new Accommodation(new Location("Subotica", "Serbia"), accommodationFeatures, accommodationRequestDTO, "someEmail");
        accommodation1.setId(1L);
        accommodation1.setPrice(12.02F);
        accommodation1.setPricePerGuest(true);
        Accommodation accommodation2 = new Accommodation(new Location("Novi Sad", "Serbia"), accommodationFeatures, accommodationRequestDTO, "someEmail");
        accommodation2.setId(2L);
        accommodation2.setPrice(13.02F);
        accommodation2.setPricePerGuest(false);
        List<Accommodation> accommodations = new ArrayList<>();
        accommodations.add(accommodation1);
        accommodations.add(accommodation2);

        when(accommodationRepository.listAll()).thenReturn(accommodations);

        List<AccommodationResponseDTO> accommodationFeatureDTOS = accommodationService.filter("", "", 0, 0, 0);

        assertEquals(accommodationFeatureDTOS.size(), 2);
    }

    @Test
    public void testRetrieveByFilters() {
        AccommodationFeature accommodationFeature = new AccommodationFeature("AC");
        List<AccommodationFeature> accommodationFeatures = new ArrayList<>();
        accommodationFeatures.add(accommodationFeature);
        List<String> photographs = new ArrayList<>();
        photographs.add("some-picture");
        AccommodationRequestDTO accommodationRequestDTO = new AccommodationRequestDTO();
        accommodationRequestDTO.setPhotographs(photographs);
        accommodationRequestDTO.setMaximumNoGuests(5);
        accommodationRequestDTO.setMinimumNoGuests(2);
        Accommodation accommodation1 = new Accommodation(new Location("Subotica", "Serbia"), accommodationFeatures, accommodationRequestDTO, "someEmail");
        accommodation1.setId(1L);
        accommodation1.setPrice(12.02F);
        accommodation1.setPricePerGuest(true);
        Accommodation accommodation2 = new Accommodation(new Location("Novi Sad", "Serbia"), accommodationFeatures, accommodationRequestDTO, "someEmail");
        accommodation2.setId(2L);
        accommodation2.setPrice(13.02F);
        accommodation2.setPricePerGuest(false);
        List<Accommodation> accommodations = new ArrayList<>();
        accommodations.add(accommodation1);
        accommodations.add(accommodation2);

        when(accommodationRepository.filter("location.id = 1 and minimumNoGuests <= 3 and maximumNoGuests >= 3 and terminated = false")).thenReturn(accommodations);
        Location location = new Location("Subotica", "Serbia");
        location.setId(1L);
        when(locationRepository.findByCityAndCountry("Subotica", "Serbia")).thenReturn(location);

        List<AccommodationResponseDTO> accommodationFeatureDTOS = accommodationService.filter("Serbia", "Subotica", 3, 0, 0);

        assertEquals(accommodationFeatureDTOS.size(), 2);
    }


}
