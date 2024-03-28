package lt.scoutress.StatisticsApp.services.playtime;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;

@Service
public class SQLiteDBService {

    private final EmployeeRepository employeeRepository;

    public SQLiteDBService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public void saveCodes(Employee employee) {
        // Gauti darbuotojo vardą ir jo kodų duomenis
        String username     = employee.getUsername();
        Integer survivalCode = employee.getSurvivalCode();
        Integer skyblockCode = employee.getSkyblockCode();
        Integer creativeCode = employee.getCreativeCode();
        Integer boxpvpCode   = employee.getBoxpvpCode();
        Integer prisonCode   = employee.getPrisonCode();
        Integer eventsCode   = employee.getEventsCode();
        
        // Sukurti naują arba atnaujinti esamą darbuotojo kodus
        Employee codes = employeeRepository.findByUsername(username);
        if (codes == null) {
            codes = new Employee(); // Sukuriamas naujas objektas, jei darbuotojas neegzistuoja
            codes.setUsername(username); // Nustatomas darbuotojo vardas
        }
        // Nustatomos arba atnaujinamos kodo reikšmės
        codes.setSurvivalCode(survivalCode);
        codes.setSkyblockCode(skyblockCode);
        codes.setCreativeCode(creativeCode);
        codes.setBoxpvpCode(boxpvpCode);
        codes.setPrisonCode(prisonCode);
        codes.setEventsCode(eventsCode);
        
        employeeRepository.save(codes); // Įrašomas arba atnaujinamas darbuotojo kodų įrašas duomenų bazėje
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // public File getSQLiteDBFile() {
    //     try {
    //         String dbFilePath = "C:\\Users\\Asus\\Documents\\Kaimux Statistics Database\\Survival.db";
    //         return new File(dbFilePath);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
}
