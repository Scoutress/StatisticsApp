package com.scoutress.KaimuxAdminStats.Repositories.DcTickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.Coefficients.DcTicketsCoef;

@Repository
public interface DcTicketsCoefRepository extends JpaRepository<DcTicketsCoef, Integer> {

    
}
