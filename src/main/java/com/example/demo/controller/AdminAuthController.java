package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "weblogic123";

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/admin-login";
    }

    @PostMapping("/login")
    public String adminLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session) {

        if (ADMIN_USERNAME.equals(username)
                && ADMIN_PASSWORD.equals(password)) {

            session.setAttribute("ADMIN_LOGGED_IN", true);
            return "redirect:/admin/dashboard";
        }

        return "redirect:/admin/login?error=true";
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}