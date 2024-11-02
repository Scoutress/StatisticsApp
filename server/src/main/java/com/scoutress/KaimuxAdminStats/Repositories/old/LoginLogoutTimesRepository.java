package com.scoutress.KaimuxAdminStats.Repositories.old;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.old.Playtime.LoginLogoutTimes;

@Repository
public interface LoginLogoutTimesRepository extends JpaRepository<LoginLogoutTimes, Long> {

  public List<LoginLogoutTimes> findByEmployeeId(Integer id);

}