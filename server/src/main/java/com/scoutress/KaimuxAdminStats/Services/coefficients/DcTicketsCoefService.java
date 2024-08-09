package com.scoutress.KaimuxAdminStats.Services.Coefficients;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.Coefficients.DcTicketsCoef;

public interface DcTicketsCoefService {

    List<DcTicketsCoef> findAll();

    DcTicketsCoef findById(int id);

    DcTicketsCoef save(DcTicketsCoef dcTicketsCoef);
    
}
