package io.github.pjazdzyk.hvacapi.infrastructure.controllers.homepage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    @GetMapping("/")
    public String showHOmePage(){
        return "index";
    }

}
