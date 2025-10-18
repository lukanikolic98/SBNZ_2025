package com.ftn.sbnz.service.controller;

import com.ftn.sbnz.service.dto.LoginRequest;
import com.ftn.sbnz.service.dto.LoginResponse;
import com.ftn.sbnz.service.dto.RegisterRequest;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.service.repository.UserRepository;
import com.ftn.sbnz.service.service.AuthService;
import com.ftn.sbnz.service.service.JwtService;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Lazy
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userService;

  @Autowired
  private AuthService authService;

  @Autowired
  private JwtService jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest authRequest, HttpServletResponse response) {
    try {
      Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(auth);

      // Check if user account is deleted or not yet activated, then proceed
      User user = userService.findByEmail(authRequest.getEmail()).orElse(null);
      if (user == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account not found"));

      }
      if (!user.isActivated() && user.getActivationToken().equals("")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "This account has been deleted"));
      } else if (user.isActivated() == false)
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("message", "Account not yet activated. Please check your email for the activation link."));

      UserDetails userDetails = (UserDetails) auth.getPrincipal();
      String accessToken = jwtUtil.generateToken(userDetails, 1000 * 60);
      String refreshToken = jwtUtil.generateTokenFromUsername(userDetails.getUsername(), 1000 * 60 * 6);

      // Set JWT as HttpOnly cookie
      return ResponseEntity.ok(new LoginResponse(
          accessToken,
          refreshToken,
          user.getEmail(),
          user.getFirstname(),
          user.getLastname(),
          user.getRole()));
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }

  @GetMapping("/confirm-registration/{token}")
  public ResponseEntity<?> confirmRegistration(@PathVariable String token) {
    try {
      authService.confirmRegistration(token);
      return ResponseEntity.ok(Map.of("message", "Activation successful"));
    } catch (Exception e) {
      // TODO: handle exception
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }

  @GetMapping("/hi")
  public String getHi(@RequestParam String param) {
    return new String("hi!" + param);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

    try {
      if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
        return ResponseEntity.badRequest().body(Map.of("message", "Email is already taken"));
      }
      User user = authService.registerRegularUser(registerRequest);
      return ResponseEntity
          .ok(Map.of("message", "User registered successfully", "activationToken", user.getActivationToken()));

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }

}
