package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Productivity;

public interface ProductivityService {

    List<Productivity> findAll();
    
}
