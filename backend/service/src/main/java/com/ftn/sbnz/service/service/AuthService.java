package com.ftn.sbnz.service.service;

import java.util.UUID;

import org.codehaus.plexus.component.annotations.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.model.models.UserRole;
import com.ftn.sbnz.service.dto.RegisterRequest;
import com.ftn.sbnz.service.repository.UserRepository;

@Service
public class AuthService {
  @Autowired
  private EmailService emailService;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  private String generateAccessToken() {
    return UUID.randomUUID().toString();
  }

  public void confirmRegistration(String token) {
    User user = userRepository.findByActivationToken(token);
    user.setActivated(true);
    user.setActivationToken("");
    userRepository.save(user);
  }

  public User registerRegularUser(RegisterRequest registerRequest) {

    // Create a new User entity from the DTO
    User user = new User();
    user.setEmail(registerRequest.getEmail());
    user.setFirstname(registerRequest.getFirstName());
    user.setLastname(registerRequest.getLastName());
    user.setActivated(false);
    user.setActivationToken(generateAccessToken());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setRole(UserRole.REGULAR_USER);
    // Save the user to the repository
    user = userRepository.save(user);

    String activationLink = "http://localhost:4200/confirm/" + user.getActivationToken();
    emailService.sendEmail(
        user.getEmail(),
        "Activate your account",
        "Click the link to activate your account: " + activationLink);
    return user;
  }
}
