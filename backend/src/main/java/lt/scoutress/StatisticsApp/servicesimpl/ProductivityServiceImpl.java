package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.Productivity;
import lt.scoutress.StatisticsApp.repositories.ProductivityRepository;
import lt.scoutress.StatisticsApp.services.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService{

    private final ProductivityRepository productivityRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository){
        this.productivityRepository = productivityRepository;
    }

    @Override
    public List<Productivity> findAll() {
        return productivityRepository.findAll();
    }
    
}
