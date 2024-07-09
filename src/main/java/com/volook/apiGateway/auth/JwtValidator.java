package com.volook.apiGateway.auth;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtValidator extends OncePerRequestFilter {
	
	@Autowired
	private JwtService tokenService;

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    	throws ServletException, IOException {
    	String token = request.getHeader("Authorization");
    	if (token != null) {
    		token = token.replace("Bearer ", "");
    		String userId = tokenService.validateToken(token);
    		String role = tokenService.validateClaim(token);
    		Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
    		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        	SecurityContextHolder.getContext().setAuthentication(authentication);
    	}
    	filterChain.doFilter(request, response);
    }
}
