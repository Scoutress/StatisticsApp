package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.entity.Coefficients.DcTicketsCoef;

@Repository
public interface DcTicketsCoefRepository extends JpaRepository<DcTicketsCoef, Integer> {

    
}
