package uns.ac.rs.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.dto.AdditionalAccommodationInfoDTO;
import uns.ac.rs.dto.AvailabilityPeriodDTO;
import uns.ac.rs.dto.SpecialAccommodationPricePeriodDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AvailabilityPeriod;
import uns.ac.rs.model.SpecialAccommodationPricePeriod;
import uns.ac.rs.repository.AccommodationRepository;
import uns.ac.rs.repository.AvailabilityPeriodRepository;
import uns.ac.rs.repository.SpecialAccommodationPricePeriodRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AvailabilityPeriodService {

    @Autowired
    private AvailabilityPeriodRepository availabilityPeriodRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private SpecialAccommodationPricePeriodRepository specialAccommodationPricePeriodRepository;

    public Accommodation changeAvailabilityPeriodAndPriceInfo(AdditionalAccommodationInfoDTO additionalAccommodationInfoDTO) {

        AvailabilityPeriodDTO availabilityPeriodDTO = additionalAccommodationInfoDTO.getAvailabilityPeriod();
        Accommodation accommodation = accommodationRepository.findById(availabilityPeriodDTO.getAccommodationId());

        if (accommodation.getAvailabilityPeriods().size() == 0) {
            createAvailabilityPeriod(availabilityPeriodDTO, accommodation);
        }
        else {
            updateAvailabilityPeriod(availabilityPeriodDTO, accommodation);
        }

        updateAccommodationGeneralPriceInfo(additionalAccommodationInfoDTO, accommodation);

        return accommodation;
    }

    private void updateAccommodationGeneralPriceInfo(AdditionalAccommodationInfoDTO additionalAccommodationInfoDTO, Accommodation accommodation) {
        accommodation.setPrice(additionalAccommodationInfoDTO.getPrice());
        accommodation.setPricePerGuest(additionalAccommodationInfoDTO.getIsPricePerGuest());
        accommodationRepository.persist(accommodation);
    }

    private void updateAvailabilityPeriod(AvailabilityPeriodDTO availabilityPeriodDTO, Accommodation accommodation) {
        AvailabilityPeriod availabilityPeriod = availabilityPeriodRepository.findById(availabilityPeriodDTO.getId());
        for (AvailabilityPeriod accommodationsAvailabilityPeriod: accommodation.getAvailabilityPeriods()) {
            if (accommodationsAvailabilityPeriod.getId() == availabilityPeriodDTO.getId()) {
                availabilityPeriod.setStartDate(accommodationsAvailabilityPeriod.getStartDate());
                availabilityPeriod.setEndDate(accommodationsAvailabilityPeriod.getEndDate());
                availabilityPeriod.setSpecialAccommodationPricePeriods(saveSpecialAccommodationPricePeriods(availabilityPeriodDTO));
                break;
            }
        }
        availabilityPeriodRepository.persist(availabilityPeriod);
    }

    private void createAvailabilityPeriod(AvailabilityPeriodDTO availabilityPeriodDTO, Accommodation accommodation) {
        List<SpecialAccommodationPricePeriod> specialAccommodationPricePeriods = saveSpecialAccommodationPricePeriods(availabilityPeriodDTO);
        AvailabilityPeriod availabilityPeriod = new AvailabilityPeriod(availabilityPeriodDTO, specialAccommodationPricePeriods);
        availabilityPeriodRepository.persist(availabilityPeriod);
        accommodation.getAvailabilityPeriods().add(availabilityPeriod);
    }

    private List<SpecialAccommodationPricePeriod> saveSpecialAccommodationPricePeriods(AvailabilityPeriodDTO availabilityPeriodDTO) {
        List<SpecialAccommodationPricePeriod> specialAccommodationPricePeriods = new ArrayList<>();
        for (SpecialAccommodationPricePeriodDTO specialAccommodationPricePeriodDTO : availabilityPeriodDTO.getSpecialAccommodationPricePeriods()) {
            SpecialAccommodationPricePeriod specialAccommodationPricePeriod = new SpecialAccommodationPricePeriod(specialAccommodationPricePeriodDTO);
            specialAccommodationPricePeriodRepository.persist(specialAccommodationPricePeriod);
            specialAccommodationPricePeriods.add(specialAccommodationPricePeriod);
        }
        return specialAccommodationPricePeriods;
    }

    public boolean areAvailabilityPeriodDatesValid(AvailabilityPeriodDTO availabilityPeriodDTO) {
        boolean datesAreValid = true;

        long newStartDate = availabilityPeriodDTO.getStartDate();
        long newEndDate = availabilityPeriodDTO.getEndDate();

        Accommodation accommodation = accommodationRepository.findById(availabilityPeriodDTO.getAccommodationId());
        for (AvailabilityPeriod availabilityPeriod: accommodation.getAvailabilityPeriods()) {

            long startDate = availabilityPeriod.getStartDate();
            long endDate = availabilityPeriod.getEndDate();

            if (isDateInRange(startDate, endDate, newStartDate) || isDateInRange(startDate, endDate, newEndDate) ||
                    (isAvailabilityPeriodInsideOfNewAvailabilityPeriod(startDate, newStartDate, endDate, newEndDate))) {
                datesAreValid = false;
                break;
            }
        }

        return datesAreValid;
    }

    public boolean areSpecialAccommodationPriceDatePeriodsValid(AvailabilityPeriodDTO availabilityPeriodDTO) {
        long startDate = availabilityPeriodDTO.getStartDate();
        long endDate = availabilityPeriodDTO.getEndDate();
        for (SpecialAccommodationPricePeriodDTO specialAccommodationPricePeriodDTO: availabilityPeriodDTO.getSpecialAccommodationPricePeriods()) {
            if (!isDateInRange(startDate, endDate, specialAccommodationPricePeriodDTO.getStartDate()) ||
            !isDateInRange(startDate, endDate, specialAccommodationPricePeriodDTO.getEndDate())) {
                return false;
            }
        }
        return true;
    }

    private boolean isDateInRange(long startDate, long endDate, long newDate) {
        return startDate <= newDate && endDate >= newDate;
    }

    private boolean isAvailabilityPeriodInsideOfNewAvailabilityPeriod(long startDate, long newStartDate, long endDate, long newEndDate) {
        return newStartDate < startDate && newEndDate > endDate;
    }
}
