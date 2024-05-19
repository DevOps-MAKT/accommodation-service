package uns.ac.rs.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.dto.AvailabilityPeriodDTO;
import uns.ac.rs.model.Accommodation;
import uns.ac.rs.model.AvailabilityPeriod;
import uns.ac.rs.repository.AccommodationRepository;
import uns.ac.rs.repository.AvailabilityPeriodRepository;

@Service
@Transactional
public class AvailabilityPeriodService {

    @Autowired
    private AvailabilityPeriodRepository availabilityPeriodRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    public Accommodation createAvailabilityPeriod(AvailabilityPeriodDTO availabilityPeriodDTO) {
        AvailabilityPeriod availabilityPeriod = new AvailabilityPeriod(availabilityPeriodDTO);
        availabilityPeriodRepository.persist(availabilityPeriod);

        Accommodation accommodation = accommodationRepository.findById(availabilityPeriodDTO.getAccommodationId());
        accommodation.getAvailabilityPeriods().add(availabilityPeriod);
        accommodationRepository.persist(accommodation);

        return accommodation;
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

    private boolean isDateInRange(long startDate, long endDate, long newDate) {
        return startDate <= newDate && endDate >= newDate;
    }

    private boolean isAvailabilityPeriodInsideOfNewAvailabilityPeriod(long startDate, long newStartDate, long endDate, long newEndDate) {
        return newStartDate < startDate && newEndDate > endDate;
    }
}
