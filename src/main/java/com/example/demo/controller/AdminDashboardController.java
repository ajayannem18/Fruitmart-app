package com.example.demo.controller;

import com.example.demo.repository.AdminUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminUserRepository adminUserRepo;

    public AdminDashboardController(AdminUserRepository adminUserRepo) {
        this.adminUserRepo = adminUserRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Boolean adminLoggedIn =
                (Boolean) session.getAttribute("ADMIN_LOGGED_IN");

        if (adminLoggedIn == null || !adminLoggedIn) {
            return "redirect:/admin/login";
        }

        model.addAttribute("users", adminUserRepo.findAll());
        return "admin/admin-dashboard";
    }
}