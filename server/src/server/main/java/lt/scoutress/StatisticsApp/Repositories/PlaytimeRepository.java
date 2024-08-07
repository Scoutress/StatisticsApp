package lt.scoutress.StatisticsApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.Playtime.Playtime;

@Repository
public interface PlaytimeRepository extends JpaRepository<Playtime, Integer>{
}
