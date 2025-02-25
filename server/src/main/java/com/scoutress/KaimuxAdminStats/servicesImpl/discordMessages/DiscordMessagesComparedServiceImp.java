package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesComparedService;

@Service
public class DiscordMessagesComparedServiceImp implements DiscordMessagesComparedService {

  private final DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository;
  private final AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository;
  private final EmployeeRepository employeeRepository;

  public DiscordMessagesComparedServiceImp(
      DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository,
      AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository,
      EmployeeRepository employeeRepository) {
    this.dailyDiscordMessagesComparedRepository = dailyDiscordMessagesComparedRepository;
    this.averageDiscordMessagesComparedRepository = averageDiscordMessagesComparedRepository;
    this.employeeRepository = employeeRepository;
  }

  @Override
  public void compareEachEmployeeDailyDiscordMessagesValues(
      List<DailyDiscordMessages> allDailyDcMessages,
      List<Short> allEmployeesFromDailyDcMessages) {

    if (allDailyDcMessages != null && !allDailyDcMessages.isEmpty()) {
      if (allEmployeesFromDailyDcMessages != null && !allEmployeesFromDailyDcMessages.isEmpty()) {

        allEmployeesFromDailyDcMessages.forEach(employeeId -> {
          double messagesRatioSumThisEmployee = 0;
          int datesCount = 0;

          LocalDate joinDateThisEmployee = getJoinDateThisEmployee(employeeId);
          LocalDate oldestDateThisEmployee = getOldestDateThisEmployee(allDailyDcMessages, employeeId);

          List<LocalDate> allDatesSinceJoinDateOrOldestDate = getAllDatesSinceJoinDateOrOldestDate(
              employeeId, allDailyDcMessages, oldestDateThisEmployee, joinDateThisEmployee);

          for (LocalDate date : allDatesSinceJoinDateOrOldestDate) {
            int messagesThisDateThisEmployee = getMessagesCountThisDateThisEmployee(
                allDailyDcMessages, date, employeeId);
            int messagesThisDateAllEmployees = getMessagesCountThisDateAllEmployees(
                allDailyDcMessages, date);
            double messagesRatioThisDateThisEmployee = calculateMessagesRatioThisDate(
                messagesThisDateThisEmployee, messagesThisDateAllEmployees);

            saveMessagesRatioThisDateThisEmployee(messagesRatioThisDateThisEmployee, date, employeeId);

            messagesRatioSumThisEmployee += messagesRatioThisDateThisEmployee;
            datesCount++;
          }

          if (datesCount > 0) {
            double averageValueOfMessagesRatiosThisEmployee = calculateAverageMessagesRatioThisEmployee(
                messagesRatioSumThisEmployee, datesCount);

            saveAverageMessagesRatioThisEmployee(averageValueOfMessagesRatiosThisEmployee, employeeId);
          }
        });
      }
    }
  }

  public List<LocalDate> getAllDatesSinceJoinDateOrOldestDate(
      Short employeeId, List<DailyDiscordMessages> rawData, LocalDate oldestDate, LocalDate joinDate) {
    LocalDate startDate = joinDate.isAfter(oldestDate) ? joinDate : oldestDate;

    return startDate
        .datesUntil(LocalDate.now())
        .collect(Collectors.toList());
  }

  public LocalDate getJoinDateThisEmployee(Short employeeId) {
    return employeeRepository
        .findById(employeeId)
        .map(Employee::getJoinDate)
        .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
  }

  public LocalDate getOldestDateThisEmployee(List<DailyDiscordMessages> rawData, Short employeeId) {
    return rawData
        .stream()
        .filter(message -> message.getEmployeeId().equals(employeeId))
        .map(DailyDiscordMessages::getDate)
        .min(Comparator.naturalOrder())
        .orElseThrow(() -> new IllegalArgumentException("No data found for the given employee ID"));
  }

  public int getMessagesCountThisDateThisEmployee(
      List<DailyDiscordMessages> data, LocalDate thisDate, Short thisEmployee) {
    return data
        .stream()
        .filter(messages -> messages.getEmployeeId().equals(thisEmployee) && messages.getDate().equals(thisDate))
        .map(DailyDiscordMessages::getMsgCount)
        .findFirst()
        .orElse(0);
  }

  public int getMessagesCountThisDateAllEmployees(List<DailyDiscordMessages> data, LocalDate thisDate) {
    return data
        .stream()
        .filter(messages -> messages.getDate().equals(thisDate))
        .mapToInt(DailyDiscordMessages::getMsgCount)
        .sum();
  }

  public double calculateMessagesRatioThisDate(int messagesThisDateThisEmployee, int messagesThisDateAllEmployees) {
    if (messagesThisDateAllEmployees == 0) {
      return 0;
    }
    return (double) messagesThisDateThisEmployee / messagesThisDateAllEmployees;
  }

  public void saveMessagesRatioThisDateThisEmployee(
      double messagesRatioThisDateThisEmployee, LocalDate date, Short employee) {
    DailyDiscordMessagesCompared existingRecord = dailyDiscordMessagesComparedRepository
        .findByEmployeeIdAndDate(employee, date);

    if (existingRecord != null) {
      existingRecord.setValue(messagesRatioThisDateThisEmployee);
      dailyDiscordMessagesComparedRepository.save(existingRecord);
    } else {
      DailyDiscordMessagesCompared newRecord = new DailyDiscordMessagesCompared();
      newRecord.setEmployeeId(employee);
      newRecord.setValue(messagesRatioThisDateThisEmployee);
      newRecord.setDate(date);
      dailyDiscordMessagesComparedRepository.save(newRecord);
    }
  }

  public double calculateAverageMessagesRatioThisEmployee(double messagesRatioSumThisEmployee, int datesCount) {
    if (datesCount == 0) {
      return 0;
    }
    return (double) messagesRatioSumThisEmployee / datesCount;
  }

  public void saveAverageMessagesRatioThisEmployee(double averageValueOfTicketRatiosThisEmployee, Short employee) {
    AverageDiscordMessagesCompared existingRecord = averageDiscordMessagesComparedRepository
        .findByEmployeeId(employee);

    if (existingRecord != null) {
      existingRecord.setValue(averageValueOfTicketRatiosThisEmployee);
      averageDiscordMessagesComparedRepository.save(existingRecord);
    } else {
      AverageDiscordMessagesCompared newRecord = new AverageDiscordMessagesCompared();
      newRecord.setEmployeeId(employee);
      newRecord.setValue(averageValueOfTicketRatiosThisEmployee);
      averageDiscordMessagesComparedRepository.save(newRecord);
    }
  }
}
