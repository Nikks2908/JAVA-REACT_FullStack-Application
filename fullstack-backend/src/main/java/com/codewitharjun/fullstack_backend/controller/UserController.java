package com.codewitharjun.fullstack_backend.controller;

import com.codewitharjun.fullstack_backend.exception.UserNotFoundException;
import com.codewitharjun.fullstack_backend.model.User;
import com.codewitharjun.fullstack_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    User newUser(@RequestBody User newUser){
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
}
