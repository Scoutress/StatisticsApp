package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.DcMessages.DcMessagesCalc;
import lt.scoutress.StatisticsApp.entity.DcMessages.DcMessagesTexted;

public interface DcMessagesService {

    List<DcMessagesTexted> findAll();

    List<DcMessagesCalc> findAllCalc();

}
