package com.qiubithub.es.controller;

import com.qiubithub.es.model.User;
import com.qiubithub.es.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
    }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/username")
    public ResponseEntity<List<User>> findByUsername(@RequestParam String username) {
        List<User> users = userService.findByUsername(username);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/email")
    public ResponseEntity<List<User>> findByEmail(@RequestParam String email) {
        List<User> users = userService.findByEmail(email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/age")
    public ResponseEntity<List<User>> findByAgeRange(
            @RequestParam int minAge, 
            @RequestParam int maxAge) {
        List<User> users = userService.findByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/gender")
    public ResponseEntity<List<User>> findByGender(@RequestParam String gender) {
        List<User> users = userService.findByGender(gender);
        return ResponseEntity.ok(users);
    }
}