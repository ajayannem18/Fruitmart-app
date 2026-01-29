package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fruitName;
    private int price;
    private int quantity;

    // ✅ OrderItem → Order
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public OrderItem() {}

    public OrderItem(String fruitName, int price, int quantity, Order order) {
        this.fruitName = fruitName;
        this.price = price;
        this.quantity = quantity;
        this.order = order;
    }

    /* ===== GETTERS ===== */

    public Long getId() { return id; }
    public String getFruitName() { return fruitName; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public Order getOrder() { return order; }

    /* ✅ CALCULATED TOTAL (NO DB COLUMN) */
    @Transient
    public int getTotal() {
        return price * quantity;
    }

    /* ===== SETTERS ===== */

    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}