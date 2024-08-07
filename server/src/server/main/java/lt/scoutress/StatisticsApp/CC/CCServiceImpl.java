package lt.scoutress.StatisticsApp.CC;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CCServiceImpl implements CCService{

    private final CCRepository ccRepository;

    public CCServiceImpl(CCRepository ccRepository) {
        this.ccRepository = ccRepository;
    }

    @Override
    public List<ContentCreator> findAll() {
        return ccRepository.findAll();
    }
    
}
