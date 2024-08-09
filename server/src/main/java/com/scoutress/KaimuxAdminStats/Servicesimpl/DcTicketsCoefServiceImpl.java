package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Coefficients.DcTicketsCoef;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketsCoefRepository;
import com.scoutress.KaimuxAdminStats.Services.Coefficients.DcTicketsCoefService;

import jakarta.persistence.EntityManager;

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
