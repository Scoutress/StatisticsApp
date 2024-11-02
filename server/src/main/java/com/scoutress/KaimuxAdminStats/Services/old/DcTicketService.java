package com.scoutress.KaimuxAdminStats.Services.old;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.old.DcTickets.DcTicket;

public interface DcTicketService {
    void saveAll(List<DcTicket> dcTickets);

    void updateDiscordTicketsAverage();

    void calculateDcTicketsPercentage();

    void updateAverageDcTicketsPercentages();
}
