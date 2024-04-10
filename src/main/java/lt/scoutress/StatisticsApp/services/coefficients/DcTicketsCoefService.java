package lt.scoutress.StatisticsApp.Services.coefficients;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Coefficients.DcTicketsCoef;

public interface DcTicketsCoefService {

    List<DcTicketsCoef> findAll();

    DcTicketsCoef findById(int id);

    DcTicketsCoef save(DcTicketsCoef dcTicketsCoef);
    
}
