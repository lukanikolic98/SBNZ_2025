package com.ftn.sbnz.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
  @NotEmpty(message = "Email is required")
  private String email;
  @NotEmpty(message = "Password is required")
  private String password;
  @NotEmpty(message = "First name is required")
  private String firstName;
  @NotEmpty(message = "Last name is required")
  private String lastName;
}
