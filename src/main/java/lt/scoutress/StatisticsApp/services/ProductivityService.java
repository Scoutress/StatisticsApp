package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.Productivity;

public interface ProductivityService {

    List<Productivity> findAll();

    List<Employee> findAllByOrderByLevel();
    
    void copyUsernamesAndLevels();

    void calculateActivityPerHalfYear();

    void checkIfEmployeeHasEnoughDaysForPromotion();

    void checkIfEmployeeLastHalfYearPlaytimeIsOK();

    //void calculateAvgPlaytimeWithCoef();
    
    //void calculateAvgAfkPlaytimeWithCoef(); //Later

    //void calculateAvgMcTicketsWithCoef();

    //void calculateAvgMcTicketsRatioWithCoef();

    //void calculateAvgDcMessagesWithCoef();

    //void calculateAvgDcMessagesRatioWithCoef();

    //void calculateComplaintsValue(); //Later

    //void calculateProductivity();

    //void decidePromoteOrDemoteOrDismiss();



    //add complaints table and calculations //later
}
