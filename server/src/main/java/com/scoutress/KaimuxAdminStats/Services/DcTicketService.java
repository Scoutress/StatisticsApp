package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;

public interface DcTicketService {
    void saveAll(List<DcTicket> dcTickets);

    void updateDiscordTicketsAverage();

    void calculateDcTicketsPercentage();

    void updateAverageDcTicketsPercentages();
}
