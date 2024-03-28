package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCalculations;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCounting;
import lt.scoutress.StatisticsApp.repositories.McTicketsCalcRepository;
import lt.scoutress.StatisticsApp.repositories.McTicketsRepository;
import lt.scoutress.StatisticsApp.services.McTicketsService;

@Service
public class McTicketsServiceImpl implements McTicketsService{

    private final McTicketsRepository mcTicketsRepository;
    private final McTicketsCalcRepository mcTicketsCalcRepository;

    public McTicketsServiceImpl(McTicketsRepository mcTicketsRepository, McTicketsCalcRepository mcTicketsCalcRepository) {
        this.mcTicketsRepository = mcTicketsRepository;
        this.mcTicketsCalcRepository = mcTicketsCalcRepository;
    }

    @Override
    public List<McTicketsCounting> findAll() {
        return mcTicketsRepository.findAll();
    }

    // @SuppressWarnings("null")
    // @Override
    // public void save(McTicketsAnswered mcTickets) {
    //     mcTicketsRepository.save(mcTickets);
    // }

    @Override
    public List<McTicketsCalculations> findAllCalc() {
        return mcTicketsCalcRepository.findAll();
    }

    @Override
    public Optional<McTicketsCounting> findById(int id) {
        return mcTicketsRepository.findById(id);
    }
}
