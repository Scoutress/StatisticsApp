package lt.scoutress.StatisticsApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.DcMessages.DcMessagesCalc;

@Repository
public interface DcMessagesCalcRepository extends JpaRepository<DcMessagesCalc, Integer> {
}
