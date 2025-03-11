package com.scoutress.KaimuxAdminStats.repositories.playtime;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountAllServers;

public interface SegmentCountAllServersRepository extends JpaRepository<SegmentCountAllServers, Long> {

  List<SegmentCountAllServers> findByEmployeeId(Short employeeId);
}
