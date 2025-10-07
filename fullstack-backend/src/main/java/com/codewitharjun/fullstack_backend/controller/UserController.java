package com.codewitharjun.fullstack_backend.controller;

import com.codewitharjun.fullstack_backend.exception.UserNotFoundException;
import com.codewitharjun.fullstack_backend.model.Roles;
import com.codewitharjun.fullstack_backend.model.User;
import com.codewitharjun.fullstack_backend.repository.RolesRepository;
import com.codewitharjun.fullstack_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin("http://localhost:3000/")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;

    @PostMapping("/user")
    User newUser(@RequestBody User newUser){
        Roles userRole = rolesRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRoles(Set.of(userRole));
        return userRepository.save(newUser);
    }

    @GetMapping("/getUsers")
    List<User> getUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/user/{id}")
    User getUser(@PathVariable Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException(id));
    }

    @PutMapping("/user/{id}")
    User updateUser(@PathVariable Long id, @RequestBody User updatedUser){
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setName(updatedUser.getName());
                    user.setEmail(updatedUser.getEmail());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @DeleteMapping("/user/{id}")
    void deleteUser(@PathVariable Long id) {
        userRepository.findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    return user;
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);

        Map<String, Object> response = new HashMap<>();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // For now: plain text password check (⚠️ not secure, should use BCrypt in production)
            if (user.getPassword().equals(password)) {
                response.put("status", "success");
                response.put("message", "Login successful");
                response.put("user", user);
            } else {
                response.put("status", "error");
                response.put("message", "Invalid password");
            }
        } else {
            response.put("status", "error");
            response.put("message", "User not found");
        }

        return response;
    }
}
