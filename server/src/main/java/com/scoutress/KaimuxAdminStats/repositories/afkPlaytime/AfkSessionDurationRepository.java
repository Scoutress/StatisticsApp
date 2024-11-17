package com.scoutress.KaimuxAdminStats.repositories.afkPlaytime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.AfkSessionDuration;

public interface AfkSessionDurationRepository extends JpaRepository<AfkSessionDuration, Long> {
}
