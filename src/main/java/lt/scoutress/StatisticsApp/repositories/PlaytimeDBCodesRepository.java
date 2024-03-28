package lt.scoutress.StatisticsApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.playtime.PlaytimeDBCodes;

@Repository
public interface PlaytimeDBCodesRepository extends JpaRepository<PlaytimeDBCodes, Integer>{
    
}
