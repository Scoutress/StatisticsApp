package com.scoutress.KaimuxAdminStats.Services.old;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.old.McTickets.McTicket;

public interface McTicketService {

    void saveAll(List<McTicket> mcTickets);

    void updateMinecraftTicketsAverage();

    void calculateMcTicketsPercentage();

    void updateAverageMcTicketsPercentages();
}
