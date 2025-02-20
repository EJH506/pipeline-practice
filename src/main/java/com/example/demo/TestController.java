package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("word", testMapper.testword());
        return "test";
    }
}
