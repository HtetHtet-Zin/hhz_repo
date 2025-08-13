package com.dat.event.controller;

import com.dat.event.common.constant.WebUrl;
import com.dat.event.service.LdapUserService;
import com.dat.event.service.StaffService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class LoginController {

    private final LdapUserService ldapUserService;
    private final StaffService staffService;

    public LoginController(LdapUserService ldapUserService, StaffService staffService) {
        this.ldapUserService = ldapUserService;
        this.staffService = staffService;
    }

    @GetMapping(WebUrl.LOGIN_URL)
    public String loginPage() {
        return "login/login-new";
    }

    @PostMapping(WebUrl.LOGIN_URL)
    public String login(@RequestParam String username,
                        @RequestParam String password, HttpSession session,
                        RedirectAttributes redirectAttributes){

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error_message", "Staff ID and Password are required!");
            return "redirect:" + WebUrl.LOGIN_URL;
        }

        if (staffService.existsByStaffNo(username, session)) {
            if (ldapUserService.isADUser(username, password)) {
                return "redirect:" + WebUrl.EVENT_URL;
            }
        }
        redirectAttributes.addFlashAttribute("error_message", "Incorrect Staff ID or Password!!!");
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @GetMapping(WebUrl.LOGOUT_URL)
    public String logoutPage(HttpSession session) {
        if (session != null && session.getAttribute("staffNo") != null) {
            session.invalidate();
        }
        return "redirect:" + WebUrl.LOGIN_URL;
    }

    @GetMapping(WebUrl.ERROR_URL)
    public String ErrorPage(HttpSession session) {
        if (session != null && session.getAttribute("staffNo") != null) {
            session.invalidate();
        }
        return "404";
    }





}