package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.DcMessages.DcMessagesCalc;

@Repository
public interface DcMessagesCalcRepository extends JpaRepository<DcMessagesCalc, Integer> {
}
