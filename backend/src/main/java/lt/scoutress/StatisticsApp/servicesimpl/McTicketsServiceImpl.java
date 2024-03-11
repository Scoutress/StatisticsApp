package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAnswered;
import lt.scoutress.StatisticsApp.repositories.McTicketsRepository;
import lt.scoutress.StatisticsApp.services.McTicketsService;

@Service
public class McTicketsServiceImpl implements McTicketsService{

    private final McTicketsRepository mcTicketsRepository;

    public McTicketsServiceImpl(McTicketsRepository mcTicketsRepository) {
        this.mcTicketsRepository = mcTicketsRepository;
    }

    @Override
    public List<McTicketsAnswered> findAll() {
        return mcTicketsRepository.findAll();
    }

    @SuppressWarnings("null")
    @Override
    public void save(McTicketsAnswered mcTickets) {
        mcTicketsRepository.save(mcTickets);
    }
}
