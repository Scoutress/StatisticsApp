package lt.scoutress.StatisticsApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.Calculations;

@Repository
public interface CalculationsRepository extends JpaRepository<Calculations, Integer> {
    
}
