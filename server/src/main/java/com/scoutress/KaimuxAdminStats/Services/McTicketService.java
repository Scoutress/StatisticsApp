package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.McTicket;

public interface McTicketService {

    List<McTicket> getAllMcTickets();

    void saveAll(List<McTicket> mcTickets);

    void updateMinecraftTicketsAverage();

    void calculateMcTicketsPercentage();

    void updateAverageMcTicketsPercentages();
}
