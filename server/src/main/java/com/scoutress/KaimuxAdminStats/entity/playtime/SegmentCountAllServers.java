package com.scoutress.KaimuxAdminStats.entity.playtime;

import com.scoutress.KaimuxAdminStats.services.HasEmployeeId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "segment_count_all_servers")
public class SegmentCountAllServers implements HasEmployeeId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "time_segment", nullable = false)
  private Integer timeSegment;

  @Column(name = "count", nullable = false)
  private Integer count;

  @Override
  public Short getEmployeeId() {
    return employeeId;
  }

  public SegmentCountAllServers(Short empId, Integer key, Integer value) {
    this.employeeId = empId;
    this.timeSegment = key;
    this.count = value;
  }
}
