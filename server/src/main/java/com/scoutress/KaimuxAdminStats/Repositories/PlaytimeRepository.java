package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.entity.Playtime.Playtime;

@Repository
public interface PlaytimeRepository extends JpaRepository<Playtime, Integer>{
}
