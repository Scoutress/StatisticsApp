// package com.scoutress.KaimuxAdminStats.Repositories.old;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import
// com.scoutress.KaimuxAdminStats.Entity.old.Employees.EmployeePromotions;
// import
// com.scoutress.KaimuxAdminStats.Entity.old.Employees.EmployeePromotionsPlus;

// public interface EmployeePromotionsRepository extends
// JpaRepository<EmployeePromotions, Long> {
// EmployeePromotions findByEmployeeId(Integer employeeId);

// @Query("SELECT new
// com.scoutress.KaimuxAdminStats.Entity.Employees.EmployeePromotionsPlus(e.id,
// e.username, e.level, ep.toSupport, ep.toChatmod, ep.toOverseer, ep.toManager)
// "
// +
// "FROM EmployeePromotions ep JOIN Employee e ON ep.employeeId = e.id " +
// "ORDER BY CASE e.level " +
// "WHEN 'Owner' THEN 1 " +
// "WHEN 'Coder' THEN 2 " +
// "WHEN 'Operator' THEN 3 " +
// "WHEN 'Manager' THEN 4 " +
// "WHEN 'Organizer' THEN 5 " +
// "WHEN 'Overseer' THEN 6 " +
// "WHEN 'ChatMod' THEN 7 " +
// "WHEN 'Support' THEN 8 " +
// "WHEN 'Helper' THEN 9 " +
// "ELSE 10 END")
// List<EmployeePromotionsPlus> findAllEmployeePromotionsWithEmployeeData();

// @Query("SELECT e.toSupport FROM EmployeePromotions e WHERE e.employeeId =
// :employeeId")
// Optional<LocalDate>
// findToSupportPromotionDateByEmployeeId(@Param("employeeId") Integer
// employeeId);

// }