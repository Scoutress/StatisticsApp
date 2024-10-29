package com.scoutress.KaimuxAdminStats.Entity;

import java.util.Objects;

public class DataItem {
  private final int id;
  private final short aid;
  private final long time;
  private final boolean action;

  public DataItem(int id, short aid, long time, boolean action) {
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

  public long getTime() {
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
        time == dataItem.time &&
        action == dataItem.action;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, aid, time, action);
  }
}
