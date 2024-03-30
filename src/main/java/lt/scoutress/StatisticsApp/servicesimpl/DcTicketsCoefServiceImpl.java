package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.coefficients.DcTicketsCoef;
import lt.scoutress.StatisticsApp.repositories.DcTicketsCoefRepository;
import lt.scoutress.StatisticsApp.services.coefficients.DcTicketsCoefService;

@Service
public class DcTicketsCoefServiceImpl implements DcTicketsCoefService{

    private final DcTicketsCoefRepository dcTicketsCoefRepository;

    public DcTicketsCoefServiceImpl(DcTicketsCoefRepository dcTicketsCoefRepository) {
        this.dcTicketsCoefRepository = dcTicketsCoefRepository;
    }

    @Override
    public List<DcTicketsCoef> findAll() {
        return dcTicketsCoefRepository.findAll();
    }
    
}
