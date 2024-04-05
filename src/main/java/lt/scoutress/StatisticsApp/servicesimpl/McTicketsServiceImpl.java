package lt.scoutress.StatisticsApp.servicesimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    public McTicketsServiceImpl(McTicketsRepository mcTicketsRepository, McTicketsCalcRepository mcTicketsCalcRepository, JdbcTemplate jdbcTemplate) {
        this.mcTicketsRepository = mcTicketsRepository;
        this.mcTicketsCalcRepository = mcTicketsCalcRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<McTicketsCounting> findAll() {
        return mcTicketsRepository.findAll();
    }

    @Override
    public List<McTicketsCalculations> findAllCalc() {
        return mcTicketsCalcRepository.findAll();
    }

    @Override
    public Optional<McTicketsCounting> findById(int id) {
        return mcTicketsRepository.findById(id);
    }
    
    @Override
    public LocalDate getOldestDate() {
        return mcTicketsRepository.findOldestDate();
    }
    
    @Override
    public LocalDate getNewestDate() {
        return mcTicketsRepository.findNewestDate();
    }

    @Override
    public boolean columnExists(String lowercaseUsername) {
        String query = "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'mc_tickets_count' AND column_name = ?";
        int count = jdbcTemplate.queryForObject(query, Integer.class, lowercaseUsername);
        return count > 0;
    }

    @Override
    public Double getTicketsCountByUsernameAndDate(String lowercaseUsername, LocalDate currentDate) {
        String query = "SELECT " + lowercaseUsername + " FROM mc_tickets_count WHERE date = ?";
        Double count = jdbcTemplate.queryForObject(query, Double.class, currentDate);
        return count;
    }

    @Override
    public void saveMcTicketsCalculations(McTicketsCalculations mcTicketsCalculations) {
        String query = "INSERT INTO mc_tickets_calculations (date, daily_tickets_sum) VALUES (?, ?)";
        jdbcTemplate.update(query, mcTicketsCalculations.getDate(), mcTicketsCalculations.getDailyTicketsSum());
    }
}
