package com.ftn.sbnz.service.filters;

import java.io.IOException;
import com.ftn.sbnz.service.CustomUserDetailsService;
import com.ftn.sbnz.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @Autowired
  private JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    // Dont include these paths in jwt check
    String path = request.getRequestURI();
    if (path.startsWith("/auth") || path.startsWith("/h2-console")) {
      chain.doFilter(request, response);
      return;
    }

    final Cookie[] cookies = request.getCookies();
    String jwt = null;
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("jwt".equals(cookie.getName())) {
          jwt = cookie.getValue();
        }
      }
    }

    String email = null;
    if (jwt != null) {
      try {
        email = jwtService.extractUsername(jwt);
      } catch (Exception e) {
        // Invalid token
        logger.warn("JWT token is invalid: " + e.getMessage());
      }
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

      if (jwtService.validateToken(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    chain.doFilter(request, response);
  }
}