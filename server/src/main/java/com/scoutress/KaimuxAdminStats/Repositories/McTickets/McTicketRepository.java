package com.scoutress.KaimuxAdminStats.Repositories.McTickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicket;

@Repository
public interface McTicketRepository extends JpaRepository<McTicket, Long> {
}
