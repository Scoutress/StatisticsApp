package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Repositories.DcMessagesCalcRepository;
import com.scoutress.KaimuxAdminStats.Repositories.DcMessagesRepository;
import com.scoutress.KaimuxAdminStats.Services.DcMessagesService;
import com.scoutress.KaimuxAdminStats.entity.DcMessages.DcMessagesCalc;
import com.scoutress.KaimuxAdminStats.entity.DcMessages.DcMessagesTexted;

@Service
public class DcMessagesServiceImpl implements DcMessagesService{

    DcMessagesRepository dcMessagesRepository;
    DcMessagesCalcRepository dcMessagesCalcRepository;

    public DcMessagesServiceImpl(DcMessagesRepository dcMessagesRepository, DcMessagesCalcRepository dcMessagesCalcRepository) {
        this.dcMessagesRepository = dcMessagesRepository;
        this.dcMessagesCalcRepository = dcMessagesCalcRepository;
    }

    @Override
    public List<DcMessagesTexted> findAll() {
        return dcMessagesRepository.findAll();
    }

    @Override
    public List<DcMessagesCalc> findAllCalc() {
        return dcMessagesCalcRepository.findAll();
    }
}
