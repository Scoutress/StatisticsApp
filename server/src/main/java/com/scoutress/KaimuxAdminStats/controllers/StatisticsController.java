package com.scoutress.KaimuxAdminStats.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.LatestActivity;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountAllServers;
import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountByServer;
import com.scoutress.KaimuxAdminStats.repositories.LatestActivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountAllServersRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountByServerRepository;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

  private final SegmentCountAllServersRepository segmentCountAllServersRepository;
  private final SegmentCountByServerRepository segmentCountByServerRepository;
  private final LatestActivityRepository latestActivityRepository;
  private final EmployeeRepository employeeRepository;

  public StatisticsController(
      SegmentCountAllServersRepository segmentCountAllServersRepository,
      SegmentCountByServerRepository segmentCountByServerRepository,
      LatestActivityRepository latestActivityRepository,
      EmployeeRepository employeeRepository) {
    this.segmentCountAllServersRepository = segmentCountAllServersRepository;
    this.segmentCountByServerRepository = segmentCountByServerRepository;
    this.latestActivityRepository = latestActivityRepository;
    this.employeeRepository = employeeRepository;
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

  @GetMapping("/latest-activity/dc-chat")
  public List<Map<String, Object>> getLatestActivityDcChat() {
    List<LatestActivity> latestActivities = latestActivityRepository.findAll();
    List<Employee> employees = employeeRepository.findAll();

    Map<Short, String> employeeIdToUsername = employees
        .stream()
        .collect(Collectors.toMap(
            Employee::getId,
            Employee::getUsername));

    List<Map<String, Object>> result = latestActivities
        .stream()
        .map(activity -> {
          Map<String, Object> map = new HashMap<>();
          map.put("employeeId", activity.getEmployeeId());
          map.put("username", employeeIdToUsername.getOrDefault(activity.getEmployeeId(), "Unknown"));
          map.put("daysSinceLastDiscordChat", activity.getDaysSinceLastDiscordChat());
          return map;
        })
        .sorted((a, b) -> {
          Number valA = (Number) a.get("daysSinceLastDiscordChat");
          Number valB = (Number) b.get("daysSinceLastDiscordChat");
          int intA = valA != null ? valA.intValue() : -1;
          int intB = valB != null ? valB.intValue() : -1;
          if (intA == -1 && intB == -1)
            return 0;
          if (intA == -1)
            return 1;
          if (intB == -1)
            return -1;
          return Integer.compare(intA, intB);
        })
        .collect(Collectors.toList());

    return result;
  }

  @GetMapping("/latest-activity/dc-ticket")
  public List<Map<String, Object>> getLatestActivityDcTickets() {
    List<LatestActivity> latestActivities = latestActivityRepository.findAll();
    List<Employee> employees = employeeRepository.findAll();

    Map<Short, String> employeeIdToUsername = employees
        .stream()
        .collect(Collectors.toMap(
            Employee::getId,
            Employee::getUsername));

    List<Map<String, Object>> result = latestActivities
        .stream()
        .map(activity -> {
          Map<String, Object> map = new HashMap<>();
          map.put("employeeId", activity.getEmployeeId());
          map.put("username", employeeIdToUsername.getOrDefault(activity.getEmployeeId(), "Unknown"));
          map.put("daysSinceLastDiscordTicket", activity.getDaysSinceLastDiscordTicket());
          return map;
        })
        .sorted((a, b) -> {
          Number valA = (Number) a.get("daysSinceLastDiscordTicket");
          Number valB = (Number) b.get("daysSinceLastDiscordTicket");
          int intA = valA != null ? valA.intValue() : -1;
          int intB = valB != null ? valB.intValue() : -1;
          if (intA == -1 && intB == -1)
            return 0;
          if (intA == -1)
            return 1;
          if (intB == -1)
            return -1;
          return Integer.compare(intA, intB);
        })
        .collect(Collectors.toList());

    return result;
  }

  @GetMapping("/latest-activity/mc-ticket")
  public List<Map<String, Object>> getLatestActivityMcTickets() {
    List<LatestActivity> latestActivities = latestActivityRepository.findAll();
    List<Employee> employees = employeeRepository.findAll();

    Map<Short, String> employeeIdToUsername = employees
        .stream()
        .collect(Collectors.toMap(
            Employee::getId,
            Employee::getUsername));

    List<Map<String, Object>> result = latestActivities
        .stream()
        .map(activity -> {
          Map<String, Object> map = new HashMap<>();
          map.put("employeeId", activity.getEmployeeId());
          map.put("username", employeeIdToUsername.getOrDefault(activity.getEmployeeId(), "Unknown"));
          map.put("daysSinceLastMinecraftTicket", activity.getDaysSinceLastMinecraftTicket());
          return map;
        })
        .sorted((a, b) -> {
          Number valA = (Number) a.get("daysSinceLastMinecraftTicket");
          Number valB = (Number) b.get("daysSinceLastMinecraftTicket");
          int intA = valA != null ? valA.intValue() : -1;
          int intB = valB != null ? valB.intValue() : -1;
          if (intA == -1 && intB == -1)
            return 0;
          if (intA == -1)
            return 1;
          if (intB == -1)
            return -1;
          return Integer.compare(intA, intB);
        })
        .collect(Collectors.toList());

    return result;
  }

  @GetMapping("/latest-activity/playtime")
  public List<Map<String, Object>> getLatestActivityPlaytime() {
    List<LatestActivity> latestActivities = latestActivityRepository.findAll();
    List<Employee> employees = employeeRepository.findAll();

    Map<Short, String> employeeIdToUsername = employees
        .stream()
        .collect(Collectors.toMap(
            Employee::getId,
            Employee::getUsername));

    List<Map<String, Object>> result = latestActivities
        .stream()
        .map(activity -> {
          Map<String, Object> map = new HashMap<>();
          map.put("employeeId", activity.getEmployeeId());
          map.put("username", employeeIdToUsername.getOrDefault(activity.getEmployeeId(), "Unknown"));
          map.put("daysSinceLastPlaytime", activity.getDaysSinceLastPlaytime());
          return map;
        })
        .sorted((a, b) -> {
          Number valA = (Number) a.get("daysSinceLastPlaytime");
          Number valB = (Number) b.get("daysSinceLastPlaytime");
          int intA = valA != null ? valA.intValue() : -1;
          int intB = valB != null ? valB.intValue() : -1;
          if (intA == -1 && intB == -1)
            return 0;
          if (intA == -1)
            return 1;
          if (intB == -1)
            return -1;
          return Integer.compare(intA, intB);
        })
        .collect(Collectors.toList());

    return result;
  }
}
