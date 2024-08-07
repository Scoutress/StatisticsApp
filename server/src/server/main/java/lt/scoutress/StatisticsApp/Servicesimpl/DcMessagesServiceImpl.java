package lt.scoutress.StatisticsApp.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.Repositories.DcMessagesCalcRepository;
import lt.scoutress.StatisticsApp.Repositories.DcMessagesRepository;
import lt.scoutress.StatisticsApp.Services.DcMessagesService;
import lt.scoutress.StatisticsApp.entity.DcMessages.DcMessagesCalc;
import lt.scoutress.StatisticsApp.entity.DcMessages.DcMessagesTexted;

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
