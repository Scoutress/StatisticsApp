package com.scoutress.KaimuxAdminStats.Entity.old.Employees;

import java.time.LocalDate;

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
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "level", nullable = false)
    private String level;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "first_name", nullable = true)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    @Column(name = "email", nullable = true, unique = true)
    private String email;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    public Employee(Integer id) {
        this.id = id;
    }

    public Employee(Integer id, LocalDate joinDate) {
        this.id = id;
        this.joinDate = joinDate;
    }

}
