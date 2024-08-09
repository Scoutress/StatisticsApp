package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.DcMessages.DcMessagesCalc;
import com.scoutress.KaimuxAdminStats.Entity.DcMessages.DcMessagesTexted;

public interface DcMessagesService {

    List<DcMessagesTexted> findAll();

    List<DcMessagesCalc> findAllCalc();

}
