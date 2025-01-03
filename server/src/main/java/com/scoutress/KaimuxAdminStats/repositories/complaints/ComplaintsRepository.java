package com.scoutress.KaimuxAdminStats.repositories.complaints;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.complaints.Complaints;

public interface ComplaintsRepository extends JpaRepository<Complaints, Long> {
}
