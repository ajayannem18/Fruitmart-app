package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fruitName;
    private int price;
    private String unit;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CartItem() {}

    public CartItem(String fruitName, int price, String unit, int quantity, User user) {
        this.fruitName = fruitName;
        this.price = price;
        this.unit = unit;
        this.quantity = quantity;
        this.user = user;
    }

    public Long getId() { return id; }
    public String getFruitName() { return fruitName; }
    public int getPrice() { return price; }
    public String getUnit() { return unit; }
    public int getQuantity() { return quantity; }
    public User getUser() { return user; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}