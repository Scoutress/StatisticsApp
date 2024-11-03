package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeLastYear;

public interface AveragePlaytimeLastYearRepository extends JpaRepository<AveragePlaytimeLastYear, Long> {
}
