package com.scoutress.KaimuxAdminStats.repositories.afkPlaytime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.AfkPlaytimeRawData;

public interface AfkPlaytimeRawDataRepository extends JpaRepository<AfkPlaytimeRawData, Long> {
}
