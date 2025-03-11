package com.scoutress.KaimuxAdminStats.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountAllServers;
import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountByServer;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountAllServersRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountByServerRepository;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

  private final SegmentCountAllServersRepository segmentCountAllServersRepository;
  private final SegmentCountByServerRepository segmentCountByServerRepository;

  public StatisticsController(
      SegmentCountAllServersRepository segmentCountAllServersRepository,
      SegmentCountByServerRepository segmentCountByServerRepository) {
    this.segmentCountAllServersRepository = segmentCountAllServersRepository;
    this.segmentCountByServerRepository = segmentCountByServerRepository;
  }

  @GetMapping("/segments/{employeeId}")
  public List<Map<String, Object>> getEmployeeStatistics(@PathVariable Short employeeId) {
    List<SegmentCountAllServers> segments = segmentCountAllServersRepository.findByEmployeeId(employeeId);

    return segments
        .stream()
        .map(segment -> {
          Map<String, Object> map = new HashMap<>();
          map.put("timeSegment", segment.getTimeSegment());
          map.put("count", segment.getCount());
          return map;
        })
        .collect(Collectors.toList());
  }

  @GetMapping("/segments/{employeeId}/{serverName}")
  public List<Map<String, Object>> getServerStatistics(
      @PathVariable Short employeeId,
      @PathVariable String serverName) {

    List<SegmentCountByServer> segments = segmentCountByServerRepository
        .findByEmployeeIdAndServerName(employeeId, serverName);

    List<Map<String, Object>> result = segments
        .stream()
        .map(segment -> {
          Map<String, Object> map = new HashMap<>();
          map.put("serverName", segment.getServerName());
          map.put("timeSegment", segment.getTimeSegment());
          map.put("count", segment.getCount());

          return map;
        })
        .collect(Collectors.toList());
    return result;
  }
}
