package org.ajay.stockSimulator.Controller;

import org.ajay.stockSimulator.DTOs.AuthRequest;
import org.ajay.stockSimulator.DTOs.AuthResponseDTO;
import org.ajay.stockSimulator.DTOs.DashboardMetricsDTO;
import org.ajay.stockSimulator.DTOs.RegistrationRequest;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.User;
import org.ajay.stockSimulator.service.JWTService;
import org.ajay.stockSimulator.service.PasswordResetService;
import org.ajay.stockSimulator.service.PortfolioItemService;
import org.ajay.stockSimulator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jWTService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    PasswordResetService passwordResetService;
    @Autowired
    private PortfolioItemService portfolioItemService;
    @GetMapping("/greeting")
    public String greeting(){
        return "Hello User you're inside the user controller right now";
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUserById(Principal principal) {

        User loggedIn = userRepo.findByUsername(principal.getName());

        if (loggedIn == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(loggedIn);
    }

    @GetMapping("/me/balance")
    public ResponseEntity<String> getUserBalance(Principal principal) {

        User user = userRepo.findByUsername(principal.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                "The current Balance is: Rs. " + user.getAmount()
        );
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUsers(@RequestBody RegistrationRequest  registrationRequest)
    {
        if(userRepo.findByUsername(registrationRequest.getUsername()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already exists");
        }
        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
        user.setAmount(new BigDecimal("10000000"));
        user.setRole("USER");
        userRepo.save(user);
        String token = jWTService.generateToken(user);


        return ResponseEntity.ok(
                new AuthResponseDTO(
                        token,
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRole()
                )
        );
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {


        try {
            // Step 1: Check if username exists
            User user = userRepo.findByUsername(authRequest.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username");
            }

            // Step 2: Check if email matches
            if (!user.getEmail().equals(authRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
//                if(user.getAmount()==null || user.getAmount().compareTo(BigDecimal.ZERO)==0)
//                {
//                    user.setAmount(new BigDecimal(10000000));
//                    userRepo.save(user);
//                }
            // Step 4: Generate token and return success
            String token = jWTService.generateToken(user);
            return ResponseEntity.ok(
                    new AuthResponseDTO(
                            token,
                            user.getFirstName(),
                            user.getLastName(),
                            user.getRole()
                    )
            );

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
    }


    @DeleteMapping("/id/me")
    public ResponseEntity<?> deleteUser(Principal principal) {

        User user = userRepo.findByUsername(principal.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        userService.deleteUser(user.getUserId());
        return ResponseEntity.ok("User has been deleted");
    }

    @PutMapping("/id/me/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(
            @RequestParam String role,
            Principal principal) {

        User user = userRepo.findByUsername(principal.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                userService.updateUserRole(user.getUserId(), role)
        );
    }

    @GetMapping("/id/me/portfolio-value")
    public ResponseEntity<Double> getUserPortfolioValue(Principal principal){

        User user = userRepo.findByUsername(principal.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                userService.getPortfolioValue(user.getUserId())
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        passwordResetService.forgotPassword(email);
        return ResponseEntity.ok(
                "If the email exists, a reset link has been sent"
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }

    @GetMapping("/me/dashboard-metrics")
    public ResponseEntity<DashboardMetricsDTO> getMetrics(
            @RequestParam String symbol,
            Principal principal) {

        User user = userService.findByUsername(principal.getName());

        DashboardMetricsDTO dto =
                portfolioItemService.getDashboardMetrics(user, symbol);

        return ResponseEntity.ok(dto);
    }

}

