package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // For profile (latest first)
    List<Order> findByUserOrderByOrderDateTimeDesc(User user);

    // For pagination (profile with pages)
    Page<Order> findByUser(User user, Pageable pageable);

    // For clear order history
    List<Order> findByUser(User user);
}