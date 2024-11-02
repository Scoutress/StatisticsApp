package com.scoutress.KaimuxAdminStats.Services.old;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.old.Productivity;

public interface ProductivityService {

    List<Productivity> findAll();

    void updateProductivity();
}
