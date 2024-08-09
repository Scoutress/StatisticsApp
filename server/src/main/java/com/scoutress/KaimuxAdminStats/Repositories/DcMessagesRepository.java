package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.DcMessages.DcMessagesTexted;

@Repository
public interface DcMessagesRepository extends JpaRepository<DcMessagesTexted, Integer> {
    
}
