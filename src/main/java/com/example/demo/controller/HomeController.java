package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final UserRepository userRepo;
    private final CartItemRepository cartRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    public HomeController(UserRepository userRepo,
                          CartItemRepository cartRepo,
                          OrderRepository orderRepo,
                          OrderItemRepository orderItemRepo) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    /* ================= LANDING / HOME ================= */

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("fruits", fruits());
        return "home";   // 🔥 public landing page
    }

    /* ================= LOGIN ================= */

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        User user = userRepo.findByEmail(email);
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
    public String doRegister(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {

        if (userRepo.findByEmail(email) != null) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userRepo.save(user);

        // ✅ auto login after register
        session.setAttribute("loggedUser", user);
        return "redirect:/dashboard";
    }

    /* ================= DASHBOARD ================= */

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        int cartCount = cartRepo.findByUser(user)
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        model.addAttribute("fruits", fruits());
        model.addAttribute("username", user.getName());
        model.addAttribute("cartCount", cartCount);

        return "dashboard";
    }

    /* ================= ADD TO CART ================= */

    @GetMapping("/add-to-cart")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam String name,
                                         @RequestParam int qty,
                                         HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");

        // ❗ Redirect if not logged in
        if (user == null) {
            return Map.of("redirect", "/login");
        }

        Map<String, Object> fruit = fruits().stream()
                .filter(f -> f.get("name").equals(name))
                .findFirst()
                .orElseThrow();

        CartItem item = cartRepo.findByUserAndFruitName(user, name)
                .orElse(new CartItem(
                        name,
                        (int) fruit.get("price"),
                        (String) fruit.get("unit"),
                        0,
                        user
                ));

        item.setQuantity(item.getQuantity() + qty);
        cartRepo.save(item);

        return Map.of("success", true);
    }

    /* ================= CART ================= */

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        List<CartItem> items = cartRepo.findByUser(user);
        int total = items.stream()
                .mapToInt(i -> i.getPrice() * i.getQuantity())
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cart";
    }

    /* ================= PLACE ORDER ================= */

    @PostMapping("/place-order")
    public String placeOrder(@RequestParam String address,
                             @RequestParam String phone,
                             @RequestParam String pincode,
                             HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartRepo.findByUser(user);
        if (cartItems.isEmpty()) return "redirect:/cart";

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPhone(phone);
        order.setPincode(pincode);
        order.setStatus("PLACED");
        order.setOrderDateTime(LocalDateTime.now());
        orderRepo.save(order);

        for (CartItem c : cartItems) {
            orderItemRepo.save(
                    new OrderItem(
                            c.getFruitName(),
                            c.getPrice(),
                            c.getQuantity(),
                            order
                    )
            );
        }

        cartRepo.deleteAll(cartItems);
        return "redirect:/profile?msg=Order placed successfully";
    }

    /* ================= PROFILE ================= */

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("orders",
                orderRepo.findByUserOrderByOrderDateTimeDesc(user));

        return "profile";
    }

    @GetMapping("/order/delete/{id}")
    public String deleteOrder(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        Order order = orderRepo.findById(id).orElse(null);
        if (order != null && order.getUser().getId().equals(user.getId())) {
            orderRepo.delete(order);
        }

        return "redirect:/profile?msg=Order deleted";
    }

    @GetMapping("/orders/clear")
    public String clearOrders(HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            orderRepo.deleteAll(orderRepo.findByUser(user));
        }

        return "redirect:/profile?msg=Order history cleared";
    }

    /* ================= LOGOUT ================= */

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /* ================= PRODUCTS ================= */

    private List<Map<String, Object>> fruits() {
        return List.of(
            fruit("Apple",180,"Kg","https://upload.wikimedia.org/wikipedia/commons/1/15/Red_Apple.jpg"),
            fruit("Banana",60,"Dozen","https://upload.wikimedia.org/wikipedia/commons/8/8a/Banana-Single.jpg"),
            fruit("Mango",250,"Kg","https://upload.wikimedia.org/wikipedia/commons/9/90/Hapus_Mango.jpg"),
            fruit("Orange",120,"Kg","https://upload.wikimedia.org/wikipedia/commons/c/c4/Orange-Fruit-Pieces.jpg"),
            fruit("Pineapple",200,"Kg","https://upload.wikimedia.org/wikipedia/commons/c/cb/Pineapple_and_cross_section.jpg"),
            fruit("Grapes",150,"Kg","https://upload.wikimedia.org/wikipedia/commons/1/11/Table_grapes_on_white.jpg"),
            fruit("Watermelon",40,"Kg","https://upload.wikimedia.org/wikipedia/commons/e/ee/Watermelon_cross_BNC.jpg"),
            fruit("Papaya",90,"Kg","https://upload.wikimedia.org/wikipedia/commons/9/9e/Papaya.jpg"),
            fruit("Strawberry",300,"Kg","https://upload.wikimedia.org/wikipedia/commons/2/29/PerfectStrawberry.jpg"),
            fruit("Kiwi",400,"Kg","https://upload.wikimedia.org/wikipedia/commons/d/d3/Kiwi_aka.jpg"),
            fruit("Pomegranate",220,"Kg","https://upload.wikimedia.org/wikipedia/commons/9/9b/PomegranateFruit.jpg"),
            fruit("Guava",80,"Kg","https://upload.wikimedia.org/wikipedia/commons/0/02/Guava_ID.jpg"),
            fruit("Chikoo",120,"Kg","https://upload.wikimedia.org/wikipedia/commons/7/76/Sapodilla.jpg"),
            fruit("Custard Apple",180,"Kg","https://upload.wikimedia.org/wikipedia/commons/e/e8/Sugar_apple.jpg"),
            fruit("Peach",260,"Kg","https://upload.wikimedia.org/wikipedia/commons/9/9f/Nectarine_and_cross_section02_edit.jpg"),
            fruit("Plum",240,"Kg","https://upload.wikimedia.org/wikipedia/commons/2/2c/Plums.jpg"),
            fruit("Cherry",600,"Kg","https://upload.wikimedia.org/wikipedia/commons/b/bb/Cherry_Stella444.jpg"),
            fruit("Blueberry",700,"Kg","https://upload.wikimedia.org/wikipedia/commons/1/12/Blueberries.jpg"),
            fruit("Avocado",350,"Kg","https://upload.wikimedia.org/wikipedia/commons/c/c4/Avocado.jpg"),
            fruit("Litchi",280,"Kg","https://upload.wikimedia.org/wikipedia/commons/4/4c/Lychee_fruit.jpg")
        );
    }

    private Map<String, Object> fruit(String n, int p, String u, String i) {
        return Map.of("name", n, "price", p, "unit", u, "image", i);
    }
}