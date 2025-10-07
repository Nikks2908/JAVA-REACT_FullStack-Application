package com.codewitharjun.fullstack_backend.controller;

import com.codewitharjun.fullstack_backend.exception.UserNotFoundException;
import com.codewitharjun.fullstack_backend.model.Roles;
import com.codewitharjun.fullstack_backend.model.User;
import com.codewitharjun.fullstack_backend.repository.RolesRepository;
import com.codewitharjun.fullstack_backend.repository.UserRepository;
import com.codewitharjun.fullstack_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin("http://localhost:3000/")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Register user with encrypted password
    @PostMapping("/user")
    User newUser(@RequestBody User newUser){
        Roles userRole = rolesRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRoles(Set.of(userRole));

        // Encrypt password before saving
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
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
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
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

    // ✅ Login API with JWT
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);

        Map<String, Object> response = new HashMap<>();

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Compare raw password with hashed password
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername());

                response.put("status", "success");
                response.put("message", "Login successful");
                response.put("token", token);
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
