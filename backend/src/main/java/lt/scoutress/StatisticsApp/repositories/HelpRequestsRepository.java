package lt.scoutress.StatisticsApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.HelpRequests;

@Repository
public interface HelpRequestsRepository extends JpaRepository<HelpRequests, Integer> {
    
}
