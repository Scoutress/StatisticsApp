package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.playtime.PlaytimeDBCodes;
import lt.scoutress.StatisticsApp.repositories.PlaytimeDBCodesRepository;
import lt.scoutress.StatisticsApp.services.playtime.PlaytimeDBCodesService;

@Service
public class PlaytimeDBCodesServiceImpl implements PlaytimeDBCodesService{

    private final PlaytimeDBCodesRepository playtimeDBCodesRepository;
    private final EntityManager entityManager;

    public PlaytimeDBCodesServiceImpl(PlaytimeDBCodesRepository playtimeDBCodesRepository, EntityManager entityManager) {
        this.playtimeDBCodesRepository = playtimeDBCodesRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<PlaytimeDBCodes> findAll() {
        return playtimeDBCodesRepository.findAll();
    }

    @Override
    @Transactional
    public void copyDBUsernames() {
        List<Employee> employees = entityManager.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();

        for (Employee employee : employees) {
            String username = employee.getUsername();

            PlaytimeDBCodes existingRecord = entityManager.createQuery("SELECT p FROM PlaytimeDBCodes p WHERE p.username = :username", PlaytimeDBCodes.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

            if (existingRecord == null) {
                PlaytimeDBCodes newRecord = new PlaytimeDBCodes();
                newRecord.setUsername(username);
                entityManager.persist(newRecord);
            }
        }
    }

    @Override
    public PlaytimeDBCodes findById(int employeeId) {
        PlaytimeDBCodes dbCodes = entityManager.find(PlaytimeDBCodes.class, employeeId);
        return dbCodes;
    }

    @Override
    public PlaytimeDBCodes save(PlaytimeDBCodes dbCodes) {
        return playtimeDBCodesRepository.save(dbCodes);
    }
}
