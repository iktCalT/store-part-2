package com.ken.store.common.controllers;

import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class HomeController {

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("time", LocalDateTime.now());
        return "index";
    }
}
