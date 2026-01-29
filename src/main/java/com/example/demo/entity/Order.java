package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Order → User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String address;
    private String phone;
    private String pincode;
    private String status;
    private LocalDateTime orderDateTime;

    // ✅ Order → OrderItems
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /* ===== GETTERS ===== */

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getPincode() { return pincode; }
    public String getStatus() { return status; }
    public LocalDateTime getOrderDateTime() { return orderDateTime; }
    public List<OrderItem> getItems() { return items; }

    /* ===== SETTERS ===== */

    public void setUser(User user) { this.user = user; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public void setStatus(String status) { this.status = status; }
    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    /* ✅ TOTAL ORDER AMOUNT (USED BY profile.html) */
    @Transient
    public int getTotal() {
        return items.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}