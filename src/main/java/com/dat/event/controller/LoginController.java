package com.dat.event.controller;


import com.dat.event.common.constant.WebUrl;
import com.dat.event.service.LdapUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(WebUrl.LOGIN_URL)
public class LoginController {

    private final LdapUserService ldapUserService;

    public LoginController(LdapUserService ldapUserService) {
        this.ldapUserService = ldapUserService;
    }

    @GetMapping
    public String loginPage() throws InterruptedException {
        return "login/login";
    }

    @PostMapping
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model){
        if(ldapUserService.isADUser(username, password)){
           return "index";
        }
        return null;
    }

}