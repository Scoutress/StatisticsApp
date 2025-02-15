package com.scoutress.KaimuxAdminStats.repositories.playtime;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;

public interface SessionDurationRepository extends JpaRepository<SessionDuration, Long> {

  @Override
  void delete(@NonNull SessionDuration sessionDuration);

  Optional<SessionDuration> findByEmployeeIdAndServerAndDateAndSingleSessionDurationInSec(
      Short employeeId, String server, LocalDate date, int singleSessionDurationInSec);

}
