package com.scoutress.KaimuxAdminStats.entity.playtime;

import com.scoutress.KaimuxAdminStats.servicesImpl.EmployeeDataServiceImpl.HasEmployeeId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "average_playtime_overall")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AveragePlaytimeOverall implements HasEmployeeId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "playtime", nullable = false)
  private Double playtime;

  @Override
  public Short getEmployeeId() {
    return employeeId;
  }
}
