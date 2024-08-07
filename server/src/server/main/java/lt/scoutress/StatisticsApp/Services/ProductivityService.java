package lt.scoutress.StatisticsApp.Services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Productivity;

public interface ProductivityService {

    List<Productivity> findAll();

    void createOrUpdateProductivityForAllEmployees();

    void copyMcTicketsValuesToProductivity();
}
