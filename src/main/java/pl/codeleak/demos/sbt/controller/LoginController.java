package pl.codeleak.demos.sbt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginError(@RequestParam("error") boolean error, RedirectAttributes redirectAttributes) {
        if (error) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        }
        return "redirect:/login";
    }

}
