package org.ajay.stockSimulator.Controller;

import org.ajay.stockSimulator.model.User;
import org.ajay.stockSimulator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/count")
    public long getUserCount() {
        return userService.countUsers();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateRole(
            @PathVariable Long userId,
            @RequestParam String role) {

        return ResponseEntity.ok(
                userService.updateUserRole(userId, role)
        );
    }

    @GetMapping("/users/{userId}/portfolio-value")
    public ResponseEntity<Double> getPortfolio(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                userService.getPortfolioValue(userId)
        );
    }
}
