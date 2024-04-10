package lt.scoutress.StatisticsApp.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class MainController {
    
    @GetMapping("")
    public String showMainMenu() {
        return "home-page";
    }
}
