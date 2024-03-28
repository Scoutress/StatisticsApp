package lt.scoutress.StatisticsApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import lt.scoutress.StatisticsApp.services.playtime.DataTransferService;

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
