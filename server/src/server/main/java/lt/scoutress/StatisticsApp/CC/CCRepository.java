package lt.scoutress.StatisticsApp.CC;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CCRepository extends JpaRepository<ContentCreator, Integer>{
    
}
