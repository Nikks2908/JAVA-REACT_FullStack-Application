package com.codewitharjun.fullstack_backend.repository;

import com.codewitharjun.fullstack_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

}
