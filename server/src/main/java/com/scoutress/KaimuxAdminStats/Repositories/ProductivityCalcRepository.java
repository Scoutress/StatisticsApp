package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.ProductivityCalc;

@Repository
public interface ProductivityCalcRepository extends JpaRepository<ProductivityCalc, Integer> {

  ProductivityCalc findByEmployeeId(Integer id);

}
