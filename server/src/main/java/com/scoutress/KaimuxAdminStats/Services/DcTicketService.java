package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.dcTickets.DcTicket;

public interface DcTicketService {
    void saveAll(List<DcTicket> dcTickets);
}
