package com.scoutress.KaimuxAdminStats.repositories.afkPlaytime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.SanitizedAfkSessionData;

public interface SanitizedAfkSessionDataRepository extends JpaRepository<SanitizedAfkSessionData, Long> {
}
