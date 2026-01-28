package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* ================= HOME ================= */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /* ================= LOGIN ================= */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        User user = userRepository.findByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        session.setAttribute("loggedUser", user);
        return "redirect:/dashboard";
    }

    /* ================= REGISTER ================= */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model,
            HttpSession session) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "User already exists");
            return "register";
        }

        User user = new User(name, email, password);
        userRepository.save(user);
        session.setAttribute("loggedUser", user);

        return "redirect:/dashboard";
    }

    /* ================= DASHBOARD ================= */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        // TEMP: allow viewing dashboard without login
        model.addAttribute("user", session.getAttribute("loggedUser"));
        model.addAttribute("fruits", getFruits());

        return "dashboard";
    }

    /* ================= ADD TO CART ================= */
    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session) {

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        return "redirect:/cart";
    }

    /* ================= CART (DISABLED) ================= */
    @GetMapping("/cart")
    public String cartDisabled(Model model) {
        model.addAttribute("feature", "Cart");
        return "feature-not-available";
    }

    /* ================= ORDERS (DISABLED) ================= */
    @GetMapping("/orders")
    public String ordersDisabled(Model model) {
        model.addAttribute("feature", "Orders");
        return "feature-not-available";
    }

    /* ================= LOGOUT ================= */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /* ================= TEMP FRUITS ================= */
    private List<Map<String, Object>> getFruits() {

        List<Map<String, Object>> fruits = new ArrayList<>();

        fruits.add(fruit("Apple", 180, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/1/15/Red_Apple.jpg"));
        fruits.add(fruit("Banana", 60, "Dozen",
                "https://upload.wikimedia.org/wikipedia/commons/8/8a/Banana-Single.jpg"));
        fruits.add(fruit("Mango", 250, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/9/90/Hapus_Mango.jpg"));
        fruits.add(fruit("Orange", 120, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/c/c4/Orange-Fruit-Pieces.jpg"));
        fruits.add(fruit("Grapes", 90, "500g",
                "https://upload.wikimedia.org/wikipedia/commons/1/15/Red_grapes.jpg"));
        fruits.add(fruit("Pineapple", 80, "Piece",
                "https://upload.wikimedia.org/wikipedia/commons/c/cb/Pineapple_and_cross_section.jpg"));
        fruits.add(fruit("Strawberry", 150, "250g",
                "https://upload.wikimedia.org/wikipedia/commons/2/29/PerfectStrawberry.jpg"));
        fruits.add(fruit("Watermelon", 40, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/e/ee/Watermelon_cross_BNC.jpg"));
        fruits.add(fruit("Papaya", 50, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/8/8b/Papaya_cross_section.jpg"));
        fruits.add(fruit("Kiwi", 200, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/d/d3/Kiwi_aka.jpg"));
        fruits.add(fruit("Pomegranate", 220, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/6/6a/Pomegranate_fruit.jpg"));
        fruits.add(fruit("Guava", 70, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/1/1c/Guava_ID.jpg"));
        fruits.add(fruit("Cherry", 300, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/b/bb/Cherry_Stella444.jpg"));
        fruits.add(fruit("Peach", 180, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/9/9e/Autumn_Red_peaches.jpg"));
        fruits.add(fruit("Plum", 190, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/4/4e/Plums.jpg"));
        fruits.add(fruit("Pear", 160, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/1/1b/Pear_DS.jpg"));
        fruits.add(fruit("Lemon", 90, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/c/c0/Lemon.jpg"));
        fruits.add(fruit("Lychee", 220, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/6/6a/Litchi_chinensis.jpg"));
        fruits.add(fruit("Blueberry", 350, "250g",
                "https://upload.wikimedia.org/wikipedia/commons/1/12/Blueberries.jpg"));
        fruits.add(fruit("Avocado", 280, "Kg",
                "https://upload.wikimedia.org/wikipedia/commons/c/c0/Avocado_Hass_-_single.jpg"));

        return fruits;
    }

    private Map<String, Object> fruit(String name, int price, String unit, String image) {
        Map<String, Object> f = new HashMap<>();
        f.put("name", name);
        f.put("price", price);
        f.put("unit", unit);
        f.put("image", image);
        return f;
    }
}
