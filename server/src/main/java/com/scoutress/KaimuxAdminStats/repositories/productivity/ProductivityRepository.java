package com.scoutress.KaimuxAdminStats.repositories.productivity;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;

public interface ProductivityRepository extends JpaRepository<Productivity, Long> {
}
