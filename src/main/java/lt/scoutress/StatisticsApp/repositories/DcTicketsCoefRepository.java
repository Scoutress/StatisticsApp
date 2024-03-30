package lt.scoutress.StatisticsApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.coefficients.DcTicketsCoef;

@Repository
public interface DcTicketsCoefRepository extends JpaRepository<DcTicketsCoef, Integer> {

    
}
