package lt.scoutress.StatisticsApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.DcMessages.DcMessagesTexted;

@Repository
public interface DcMessagesRepository extends JpaRepository<DcMessagesTexted, Integer> {
    
}
