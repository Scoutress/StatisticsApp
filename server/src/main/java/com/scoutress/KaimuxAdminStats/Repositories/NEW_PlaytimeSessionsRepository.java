package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.LoginLogoutTimes;

public interface NEW_PlaytimeSessionsRepository extends JpaRepository<LoginLogoutTimes, Long> {

}
