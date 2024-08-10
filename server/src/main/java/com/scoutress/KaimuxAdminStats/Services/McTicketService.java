package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicket;

public interface McTicketService {

    void saveAll(List<McTicket> mcTickets);

    void updateMinecraftTicketsAverage();

    void calculateMcTicketsPercentage();

    void updateAverageMcTicketsPercentages();
}
