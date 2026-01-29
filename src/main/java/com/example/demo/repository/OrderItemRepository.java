package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ✅ Correct relationship
    List<OrderItem> findByOrder(Order order);
}