package energyservice;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AssignRepository extends PagingAndSortingRepository<Assign, Long>{

    Assign findByReservationid(Long reservationid);
}