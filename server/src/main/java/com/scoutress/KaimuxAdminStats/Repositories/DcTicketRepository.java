package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.entity.dcTickets.DcTicket;

@Repository
public interface DcTicketRepository extends JpaRepository<DcTicket, Long> {
}