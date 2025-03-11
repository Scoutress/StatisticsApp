package com.scoutress.KaimuxAdminStats.repositories.playtime;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountByServer;

public interface SegmentCountByServerRepository extends JpaRepository<SegmentCountByServer, Long> {

  List<SegmentCountByServer> findByEmployeeIdAndServerName(Short employeeId, String serverName);
}
