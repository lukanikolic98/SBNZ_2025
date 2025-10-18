package com.ftn.sbnz.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.ftn.sbnz.model.models.UserRole;

@Data
@AllArgsConstructor
public class LoginResponse {
  private String accessToken;
  private String refreshToken;
  private String email;
  private String firstname;
  private String lastname;
  private UserRole role;
}
