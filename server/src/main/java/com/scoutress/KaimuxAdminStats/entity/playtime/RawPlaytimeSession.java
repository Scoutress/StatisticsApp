package com.scoutress.KaimuxAdminStats.entity.playtime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawPlaytimeSession {

  @Id
  private Long id;
  private int userId;
  private int time;
  private int action;
}
