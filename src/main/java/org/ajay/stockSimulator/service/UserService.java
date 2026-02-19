package org.ajay.stockSimulator.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.ajay.stockSimulator.Repo.PortfolioItemRepo;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserService {
    @Autowired
    private UserRepo userRepo;
@Autowired
JWTService jwtService;
@Autowired
AuthenticationManager authenticationManager;
@Autowired
PasswordEncoder passwordEncoder;
    @Autowired
    private PortfolioItemRepo portfolioItemRepo;

    public User registerUser(User user) {
       return userRepo.save(user);
    }
    public Boolean validateUser(String username,String password){
        User user = userRepo.findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());

    }

    public Optional<User> getUserById(long userId) {
        return userRepo.findByUserId(userId);
    }

    public User addUserById(User user) {
        return userRepo.save(user);
    }

    public User registerUsers(User user) {
        return userRepo.save(user);
    }

    public String verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user);
        }
        else  {
            return "Fail";
        }

    }



    public long countUsers() {
        return userRepo.count();
    }

    public List<User> getAllUsers() {
     return   userRepo.findAll();
    }

    public void deleteUser(long id) {
        userRepo.deleteById(id);
    }

    public User updateUserRole(long id, String role) {
     User user = userRepo.findById(id)
     .orElseThrow(() -> new RuntimeException("User not found!"));
        user.setRole(role);
        return userRepo.save(user);
    }

    public Double getPortfolioValue(long id) {
        Double value  = portfolioItemRepo.getUserPortfolioValue(id);
        return value!=null?value:0.0;
    }

    @Transactional
    public void incrementTradeCount(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setTradeCount(user.getTradeCount() + 1);

        userRepo.save(user);
    }

    public User findByUsername(String username) {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        return user;
    }

}
