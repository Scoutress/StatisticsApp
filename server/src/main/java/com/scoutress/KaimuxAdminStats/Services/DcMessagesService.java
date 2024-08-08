package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.DcMessages.DcMessagesCalc;
import com.scoutress.KaimuxAdminStats.entity.DcMessages.DcMessagesTexted;

public interface DcMessagesService {

    List<DcMessagesTexted> findAll();

    List<DcMessagesCalc> findAllCalc();

}
