package com.scoutress.KaimuxAdminStats.CC;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cc")
public class CCController {

    private final CCService ccService;
    
    public CCController(CCService ccService) {
        this.ccService = ccService;
    }

    @GetMapping("/list")
    public String getAllCCs(Model model){
        List<ContentCreator> contentCreator = ccService.findAll();
        model.addAttribute("ccs", contentCreator);
        return "cc/all-ccs";
    }
}
