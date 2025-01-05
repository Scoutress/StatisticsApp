package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.LoginLogoutTimes;

public interface LoginLogoutTimesRepository extends JpaRepository<LoginLogoutTimes, Long> {
}
