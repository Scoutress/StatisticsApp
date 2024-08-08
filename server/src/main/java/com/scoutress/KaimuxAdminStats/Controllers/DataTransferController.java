package com.scoutress.KaimuxAdminStats.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.scoutress.KaimuxAdminStats.Services.playtime.DataTransferService;

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
