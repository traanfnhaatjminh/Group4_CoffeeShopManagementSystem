package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

import java.io.IOException;

@Controller
public class UsersController {

    @Autowired
    private UserService userService;
    //get user profile info
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Users user = userService.getLoggedInUser();
        model.addAttribute("user", user);
        return "profile";
    }

    //    @GetMapping("/profile/edit")
//    public String editProfile(Model model) {
//        Users user = userService.getLoggedInUser();
//        model.addAttribute("user", user);
//        return "editProfile";
//    }
    //update new info of user by edit profile
    @PostMapping("/profile")
    @ResponseBody
    public String saveProfile(@RequestParam("fullname") String fullname,
                              @RequestParam("dob") String dob,
                              @RequestParam("email") String email,
                              @RequestParam("phone") String phone,
                              @RequestParam("address") String address,
                              @RequestParam("username") String username,
                              @RequestParam("avatar") MultipartFile avatarFile) {
        try {
            Users loggedInUser = userService.getLoggedInUser();
            loggedInUser.setFullname(fullname);
            loggedInUser.setDob(dob);
            loggedInUser.setEmail(email);
            loggedInUser.setPhone(phone);
            loggedInUser.setAddress(address);
            loggedInUser.setUsername(username);

            if (!avatarFile.isEmpty()) {
                String avatarUrl = userService.saveAvatar(avatarFile);
                loggedInUser.setAvatar(avatarUrl);
            }

            userService.saveUser(loggedInUser);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
