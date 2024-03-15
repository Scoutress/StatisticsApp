package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.Productivity;

public interface ProductivityService {

    List<Productivity> findAll();

    List<Employee> findAllByOrderByLevel();
    
    void copyUsernamesAndLevels();
}
