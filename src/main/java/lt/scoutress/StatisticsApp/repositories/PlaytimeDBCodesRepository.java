package lt.scoutress.StatisticsApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.Playtime.PlaytimeDBCodes;

@Repository
public interface PlaytimeDBCodesRepository extends JpaRepository<PlaytimeDBCodes, Integer>{
    
}
