package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.HelpRequests;
import lt.scoutress.StatisticsApp.repositories.HelpRequestsRepository;
import lt.scoutress.StatisticsApp.services.StatisticsService;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final HelpRequestsRepository helpRequestsRepository;

    public StatisticsServiceImpl(HelpRequestsRepository helpRequestsRepository) {
        this.helpRequestsRepository = helpRequestsRepository;
    }

    @Override
    public String showForm() {
        return null;
    }

    @Override
    public List<HelpRequests> findAllHelpRequests() {
        return helpRequestsRepository.findAll();
    }
}
