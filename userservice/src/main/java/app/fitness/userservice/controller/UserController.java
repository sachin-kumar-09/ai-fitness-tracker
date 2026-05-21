package app.fitness.userservice.controller;

import app.fitness.userservice.dto.RegisterRequest;
import app.fitness.userservice.dto.UserResponse;
import app.fitness.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Entering UserController :: register at {}", System.currentTimeMillis());
        log.info("Payload: {}", request);
        try {
            return ResponseEntity.ok(userService.register(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User already exists.");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(@RequestParam String email) {
        log.info("Entering UserController :: getUserProfile at {}", System.currentTimeMillis());
        log.info("Payload: {}", email);
        try {
            return ResponseEntity.ok(userService.getUserProfile(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUserId(@PathVariable String userId) {
        log.info("Entering UserController :: validateUserId at {}", System.currentTimeMillis());
        log.info("Payload: {}", userId);
        return ResponseEntity.ok(userService.existsByUserId(userId));
    }

}
