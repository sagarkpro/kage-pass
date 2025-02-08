package com.sagar.kagepass.jwt;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagar.kagepass.exceptions.InvalidTokenException;
import com.sagar.kagepass.services.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
            try{
                String username = jwtUtils.extractUsername(bearerToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = userService.loadUserByUsername(username);
                    if (user != null) {
                        if (jwtUtils.verifyToken(bearerToken, user)) {
                            var authenticationToken = 
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }
            }
            catch(InvalidTokenException ex){
                handleInvalidTokenException(response, ex);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void handleInvalidTokenException(HttpServletResponse response, InvalidTokenException ex) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        String errorResponse = objectMapper.writeValueAsString(Map.of("error", "Invalid token"));
        response.getWriter().write(errorResponse);
    }

}
