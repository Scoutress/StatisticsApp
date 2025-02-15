package com.scoutress.KaimuxAdminStats.repositories.complaints;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.complaints.ComplaintsSum;

public interface ComplaintsSumRepository extends JpaRepository<ComplaintsSum, Long> {

  ComplaintsSum findByEmployeeId(Short employeeId);
}
