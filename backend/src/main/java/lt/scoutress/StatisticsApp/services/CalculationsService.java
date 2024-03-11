package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Calculations;

public interface CalculationsService {
    
    public List<Calculations> findCalculations();

    void calculateDaysSinceJoinAndSave();

}
