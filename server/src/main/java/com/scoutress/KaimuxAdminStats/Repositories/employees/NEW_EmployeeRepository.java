package com.scoutress.KaimuxAdminStats.Repositories.employees;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_Employee;

public interface NEW_EmployeeRepository extends JpaRepository<NEW_Employee, Long> {
}
