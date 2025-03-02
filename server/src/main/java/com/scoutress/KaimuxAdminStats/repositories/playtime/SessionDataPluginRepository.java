package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataPlugin;

public interface SessionDataPluginRepository extends JpaRepository<SessionDataPlugin, Long> {

}
