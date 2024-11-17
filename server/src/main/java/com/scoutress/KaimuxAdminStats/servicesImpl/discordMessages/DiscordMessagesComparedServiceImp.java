package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesComparedService;

@Service
public class DiscordMessagesComparedServiceImp implements DiscordMessagesComparedService {

  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository;

  public DiscordMessagesComparedServiceImp(
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository) {
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.dailyDiscordMessagesComparedRepository = dailyDiscordMessagesComparedRepository;
  }

  @Override
  public void compareEachEmployeeDailyDcMsgsValues() {
    List<DailyDiscordMessages> dailyMessages = dailyDiscordMessagesRepository.findAll();

    Map<String, List<DailyDiscordMessages>> groupedMessages = groupMessagesByAidAndDate(dailyMessages);

    for (Map.Entry<String, List<DailyDiscordMessages>> entry : groupedMessages.entrySet()) {
      Short aid = extractAid(entry.getKey());
      LocalDate date = extractDate(entry.getKey());
      int totalMessages = calculateTotalMessages(entry.getValue());
      saveComparedData(entry.getValue(), aid, date, totalMessages);
    }
  }

  private Map<String, List<DailyDiscordMessages>> groupMessagesByAidAndDate(List<DailyDiscordMessages> dailyMessages) {
    return dailyMessages
        .stream()
        .collect(Collectors.groupingBy(
            msg -> msg.getAid() + "_" + msg.getDate()));
  }

  private Short extractAid(String key) {
    String[] keyParts = key.split("_");
    return Short.valueOf(keyParts[0]);
  }

  private LocalDate extractDate(String key) {
    String[] keyParts = key.split("_");
    return LocalDate.parse(keyParts[1]);
  }

  private int calculateTotalMessages(List<DailyDiscordMessages> messages) {
    return messages
        .stream()
        .mapToInt(DailyDiscordMessages::getMsgCount)
        .sum();
  }

  private void saveComparedData(List<DailyDiscordMessages> messages, Short aid, LocalDate date, int totalMessages) {
    for (DailyDiscordMessages msg : messages) {
      double value = calculateMessageValue(msg.getMsgCount(), totalMessages);
      DailyDiscordMessagesCompared comparedData = new DailyDiscordMessagesCompared(
          null,
          aid,
          value,
          date);
      dailyDiscordMessagesComparedRepository.save(comparedData);
    }
  }

  private double calculateMessageValue(int messageCount, int totalMessages) {
    return (double) messageCount / totalMessages;
  }
}
