package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;

public interface SanitazedDataRepository extends JpaRepository<SanitizedSessionData, Long> {
}
