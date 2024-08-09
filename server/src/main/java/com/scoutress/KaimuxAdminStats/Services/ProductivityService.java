package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.Productivity;

public interface ProductivityService {

    List<Productivity> findAll();

    void updateProductivityData();
}
