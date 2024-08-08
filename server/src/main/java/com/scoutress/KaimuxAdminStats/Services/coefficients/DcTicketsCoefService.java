package com.scoutress.KaimuxAdminStats.Services.coefficients;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.Coefficients.DcTicketsCoef;

public interface DcTicketsCoefService {

    List<DcTicketsCoef> findAll();

    DcTicketsCoef findById(int id);

    DcTicketsCoef save(DcTicketsCoef dcTicketsCoef);
    
}
