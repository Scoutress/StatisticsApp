package lt.scoutress.StatisticsApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("SELECT e FROM Employee e ORDER BY CASE e.level " +
            "WHEN 'Owner' THEN 1 " +
            "WHEN 'Coder' THEN 2 " +
            "WHEN 'Operator' THEN 3 " +
            "WHEN 'Manager' THEN 4 " +
            "WHEN 'Organizer' THEN 5 " +
            "WHEN 'Overseer' THEN 6 " +
            "WHEN 'ChatMod' THEN 7 " +
            "WHEN 'Support' THEN 8 " +
            "WHEN 'Helper' THEN 9 " +
            "ELSE 10 END")
    public List<Employee> findAllByOrderByLevel();

    public Employee findByUsername(String username);

}

