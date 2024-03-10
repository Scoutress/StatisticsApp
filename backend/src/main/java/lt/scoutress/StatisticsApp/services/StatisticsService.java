package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.HelpRequests;

public interface StatisticsService {
    
    public String showForm();

    public List<HelpRequests> findAllHelpRequests();

}
