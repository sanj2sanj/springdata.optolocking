package org.sanj2sanj.springdata.optolocking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {

    @RequestMapping("/hello")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/greeting")
    public String greeting(
            @RequestParam(value = "datePlanted", required = false, defaultValue = "World") String name,
            Model model) {
        model.addAttribute("datePlanted", name);
        model.addAttribute("seedStarter", name);
        return "greeting";
    }
}