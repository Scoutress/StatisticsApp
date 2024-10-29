package com.scoutress.KaimuxAdminStats.Entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class DataItem {
  private final int id;
  private final short aid;
  private final LocalDateTime time;

  public DataItem(int id, short aid, LocalDateTime time) {
    this.id = id;
    this.aid = aid;
    this.time = time;
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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DataItem dataItem = (DataItem) o;
    return id == dataItem.id && aid == dataItem.aid && Objects.equals(time, dataItem.time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, aid, time);
  }
}
