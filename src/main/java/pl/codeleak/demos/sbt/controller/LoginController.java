


package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginError(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             RedirectAttributes redirectAttributes) {
        Users user = userService.getUserByUsername(username);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        } else if (!userService.checkPassword(user, password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        } else if (user.getStatus() != 1) {
            redirectAttributes.addFlashAttribute("error", "Account is not active");
        } else {

            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }
}

