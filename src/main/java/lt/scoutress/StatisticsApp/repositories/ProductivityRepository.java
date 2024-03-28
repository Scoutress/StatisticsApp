package lt.scoutress.StatisticsApp.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import lt.scoutress.StatisticsApp.entity.Productivity;

@Repository
public interface ProductivityRepository extends JpaRepository<Productivity, Integer> {
    
}
