package com.dat.event.controller;


import com.dat.event.common.constant.WebUrl;
import com.dat.event.service.LdapUserService;
import com.dat.event.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(WebUrl.LOGIN_URL)
public class LoginController {

    private final LdapUserService ldapUserService;
    private final StaffService staffService;

    public LoginController(LdapUserService ldapUserService, StaffService staffService) {
        this.ldapUserService = ldapUserService;
        this.staffService = staffService;
    }

    @GetMapping
    public String loginPage() {
        return "login/login";
    }

    @PostMapping
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes){

        if (staffService.existsByStaffNo(username)) {
            if(ldapUserService.isADUser(username, password)){
                return "index";
            }
        }
        System.out.println("Incorrect Staff ID or Password");
        redirectAttributes.addFlashAttribute("error_message", "Incorrect Staff ID or Password!!!");
        return "redirect:/login";
    }

}