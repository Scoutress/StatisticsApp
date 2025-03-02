package com.scoutress.KaimuxAdminStats.repositories.playtime;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.LoginLogoutTimes;

public interface LoginLogoutTimesRepository extends JpaRepository<LoginLogoutTimes, Long> {

  List<LoginLogoutTimes> findByLoginTimeGreaterThanEqual(LocalDateTime loginTime);
}
