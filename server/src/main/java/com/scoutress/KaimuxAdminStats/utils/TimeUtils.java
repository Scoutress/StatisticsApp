package com.scoutress.KaimuxAdminStats.utils;

import java.time.LocalDateTime;

public class TimeUtils {

  public static int toMinutesOfDay(LocalDateTime time) {
    return time.getHour() * 60 + time.getMinute();
  }
}
