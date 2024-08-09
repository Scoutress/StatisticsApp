package com.scoutress.KaimuxAdminStats.Repositories.DcTickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;

@Repository
public interface DcTicketRepository extends JpaRepository<DcTicket, Long> {
}