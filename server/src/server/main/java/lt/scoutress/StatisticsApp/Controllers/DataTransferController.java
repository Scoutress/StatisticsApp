package lt.scoutress.StatisticsApp.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import lt.scoutress.StatisticsApp.Services.playtime.DataTransferService;

@Controller
public class DataTransferController {

    @Autowired
    private DataTransferService dataTransferService;

    @PostMapping("/transfer-data")
    public String transferData() {
        dataTransferService.transferDataFromSQLiteToMySQL();
        return "redirect:/";
    }
}
