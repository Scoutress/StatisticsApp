package lt.scoutress.StatisticsApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.Coefficients.DcTicketsCoef;

@Repository
public interface DcTicketsCoefRepository extends JpaRepository<DcTicketsCoef, Integer> {

    
}
