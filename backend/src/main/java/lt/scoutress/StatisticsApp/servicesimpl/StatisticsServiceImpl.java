package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.McTickets;
import lt.scoutress.StatisticsApp.repositories.McTicketsRepository;
import lt.scoutress.StatisticsApp.services.StatisticsService;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final McTicketsRepository mcTicketsRepository;

    public StatisticsServiceImpl(McTicketsRepository mcTicketsRepository) {
        this.mcTicketsRepository = mcTicketsRepository;
    }

    @Override
    public String showForm() {
        return null;
    }

    @Override
    public List<McTickets> findAllMcTickets() {
        return mcTicketsRepository.findAll();
    }
}
