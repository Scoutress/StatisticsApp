package com.scoutress.KaimuxAdminStats.Entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class DataItem {
  private final int id;
  private final short aid;
  private final LocalDateTime time;
  private final boolean action;

  public DataItem(int id, short aid, LocalDateTime time, boolean action) {
    this.id = id;
    this.aid = aid;
    this.time = time;
    this.action = action;
  }

  public int getId() {
    return id;
  }

  public short getAid() {
    return aid;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public boolean isAction() {
    return action;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DataItem dataItem = (DataItem) o;
    return id == dataItem.id &&
        aid == dataItem.aid &&
        action == dataItem.action &&
        Objects.equals(time, dataItem.time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, aid, time, action);
  }
}
