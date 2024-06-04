package uns.ac.rs.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uns.ac.rs.dto.AdditionalAccommodationInfoDTO;
import uns.ac.rs.dto.AvailabilityPeriodDTO;
import uns.ac.rs.dto.SpecialAccommodationPricePeriodDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AvailabilityPeriod;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvailabilityPeriodServiceTests {

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

        boolean result = availabilityPeriodService.areAvailabilityPeriodDatesValid(availabilityPeriodDTO);

        assertFalse(result);
    }

    @Test
    void testAreSpecialAccommodationPriceDatePeriodsValid_OverlappingPeriods() {

        AvailabilityPeriodDTO availabilityPeriodDTO = new AvailabilityPeriodDTO();
        availabilityPeriodDTO.setStartDate(1000000000L);
        availabilityPeriodDTO.setEndDate(2000000000L);
        availabilityPeriodDTO.setAccommodationId(1L);

        SpecialAccommodationPricePeriodDTO specialAccommodationPricePeriodDTO = new SpecialAccommodationPricePeriodDTO();
        specialAccommodationPricePeriodDTO.setStartDate(2000000001L);
        specialAccommodationPricePeriodDTO.setEndDate(2100000000L);
        List<SpecialAccommodationPricePeriodDTO> specialAccommodationPricePeriods = new ArrayList<>();
        specialAccommodationPricePeriods.add(specialAccommodationPricePeriodDTO);
        availabilityPeriodDTO.setSpecialAccommodationPricePeriods(specialAccommodationPricePeriods);

        boolean result = availabilityPeriodService.areSpecialAccommodationPriceDatePeriodsValid(availabilityPeriodDTO);

        assertFalse(result);
    }

    @Test
    void testAreSpecialAccommodationPriceDatePeriodsValid_NoOverlappingPeriods() {

        AvailabilityPeriodDTO availabilityPeriodDTO = new AvailabilityPeriodDTO();
        availabilityPeriodDTO.setStartDate(1000000000L);
        availabilityPeriodDTO.setEndDate(2000000000L);
        availabilityPeriodDTO.setAccommodationId(1L);

        SpecialAccommodationPricePeriodDTO specialAccommodationPricePeriodDTO = new SpecialAccommodationPricePeriodDTO();
        specialAccommodationPricePeriodDTO.setStartDate(1200000000L);
        specialAccommodationPricePeriodDTO.setEndDate(1400000000L);
        List<SpecialAccommodationPricePeriodDTO> specialAccommodationPricePeriods = new ArrayList<>();
        specialAccommodationPricePeriods.add(specialAccommodationPricePeriodDTO);
        availabilityPeriodDTO.setSpecialAccommodationPricePeriods(specialAccommodationPricePeriods);

        boolean result = availabilityPeriodService.areSpecialAccommodationPriceDatePeriodsValid(availabilityPeriodDTO);

        assertTrue(result);
    }

    @Test
    public void testCreateAvailabilityPeriod() {

        AvailabilityPeriodDTO availabilityPeriodDTO = new AvailabilityPeriodDTO();
        availabilityPeriodDTO.setStartDate(1716156000000L);
        availabilityPeriodDTO.setEndDate(1716328800000L);
        availabilityPeriodDTO.setAccommodationId(1L);

        AdditionalAccommodationInfoDTO additionalAccommodationInfoDTO = new AdditionalAccommodationInfoDTO();
        additionalAccommodationInfoDTO.setAvailabilityPeriod(availabilityPeriodDTO);
        additionalAccommodationInfoDTO.setIsAvailabilityPeriodBeingUpdated(false);

        Accommodation accommodation = new Accommodation();
        List<AvailabilityPeriod> availabilityPeriods = new ArrayList<>();
        accommodation.setAvailabilityPeriods(availabilityPeriods);
        when(accommodationRepository.findById(availabilityPeriodDTO.getAccommodationId())).thenReturn(accommodation);

        Accommodation result = availabilityPeriodService.changeAvailabilityPeriod(additionalAccommodationInfoDTO);

        assertEquals(result.getAvailabilityPeriods().size(), 1);
        verify(availabilityPeriodRepository).persist(any(AvailabilityPeriod.class));
        verify(accommodationRepository).findById(availabilityPeriodDTO.getAccommodationId());
        verify(accommodationRepository).persist(any(Accommodation.class));
    }



}
