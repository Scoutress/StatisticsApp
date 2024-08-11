package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.Productivity;

public interface ProductivityService {

    List<Productivity> findAll();

    void updateProductivityData();

    void updateAnnualPlaytimeForAllEmployees();

    void updateAveragePlaytimeForAllEmployees();

    void updateAfkPlaytimeForAllEmployees();

    void calculateServerTicketsForAllEmployeesWithCoefs();

    void calculateServerTicketsTakenForAllEmployeesWithCoefs();

    void calculatePlaytimeForAllEmployeesWithCoefs();

    void calculateAfkPlaytimeForAllEmployeesWithCoefs();

    void calculateAnsweredDiscordTicketsWithCoefs();

    void calculateAndSaveComplainsCalc();

    void calculateAndSaveProductivity();

    void calculateAveragePlaytime();
}
