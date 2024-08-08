package com.scoutress.KaimuxAdminStats.Repositories.McTickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.entity.McTickets.McTicketsAvgDailyRatio;

@Repository
public interface McTicketsAvgDailyRatioRepository extends JpaRepository<McTicketsAvgDailyRatio, Integer> {

}
