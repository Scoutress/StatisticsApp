package lt.scoutress.StatisticsApp.services.coefficients;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.coefficients.DcTicketsCoef;

public interface DcTicketsCoefService {

    List<DcTicketsCoef> findAll();

    DcTicketsCoef findById(int id);

    DcTicketsCoef save(DcTicketsCoef dcTicketsCoef);
    
}
