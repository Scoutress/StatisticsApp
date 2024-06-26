package lt.scoutress.StatisticsApp.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import lt.scoutress.StatisticsApp.Repositories.DcTicketsCoefRepository;
import lt.scoutress.StatisticsApp.Services.coefficients.DcTicketsCoefService;
import lt.scoutress.StatisticsApp.entity.Coefficients.DcTicketsCoef;

@Service
public class DcTicketsCoefServiceImpl implements DcTicketsCoefService{

    private final DcTicketsCoefRepository dcTicketsCoefRepository;
    private final EntityManager entityManager;

    public DcTicketsCoefServiceImpl(DcTicketsCoefRepository dcTicketsCoefRepository, 
        EntityManager entityManager) {
        this.dcTicketsCoefRepository = dcTicketsCoefRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<DcTicketsCoef> findAll() {
        return dcTicketsCoefRepository.findAll();
    }

    @Override
    public DcTicketsCoef findById(int id) {
        DcTicketsCoef dcTicketsCoef = entityManager.find(DcTicketsCoef.class, id);
        return dcTicketsCoef;
    }

    @Override
    public DcTicketsCoef save(DcTicketsCoef dcTicketsCoef) {
        return dcTicketsCoefRepository.save(dcTicketsCoef);
    }
}
