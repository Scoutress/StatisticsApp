package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService {

    private final ProductivityRepository productivityRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository) {
        this.productivityRepository = productivityRepository;
    }

    @Override
    public List<Productivity> findAll() {
        return productivityRepository.findAllWithEmployeeDetails();
    }
}
