package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // find user by email (used for login & validation)
    User findByEmail(String email);

    // check if email already exists (for registration)
    boolean existsByEmail(String email);
}