package com.volook.apiGateway.userManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.MediaType;
import com.volook.apiGateway.auth.JwtService;
import com.volook.apiGateway.userManagement.dto.JwtDto;
import com.volook.apiGateway.userManagement.dto.LoginDto;
import com.volook.apiGateway.userManagement.services.UserService;

import userManager.UserOuterClass.Role;
import userManager.UserOuterClass.User;

@RestController
@RequestMapping("user")
public class UserLoggedController {
	
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<JwtDto> login(@RequestBody LoginDto data) {
		try {
			String accessToken = jwtService.generateJwtToken(data.emailAddress(), data.password());
			JwtDto jwt;
			if(accessToken != null) {
				jwt = new JwtDto(accessToken);
				return ResponseEntity.ok(jwt);
			}
		}catch(Exception e) {
		}
		return new ResponseEntity<JwtDto>( HttpStatus.UNAUTHORIZED);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<JwtDto> signin(@RequestBody LoginDto data) {
		User userDto = User.newBuilder()
				.setEmail(data.emailAddress())
				.setSaltedPassword(data.password())
				.setRole(Role.CUSTOMER)
				.build();
		User user = this.userService.saveOrUpdate(userDto);
		JwtDto jwt;
		if(user!=null) {
			String accessToken = this.jwtService.generateJwtToken(data.emailAddress(), data.password());
			if(accessToken != null) {
				jwt = new JwtDto(accessToken);
				return ResponseEntity.ok(jwt);
			}
		}
		jwt = new JwtDto("");
		return new ResponseEntity<JwtDto>(jwt,HttpStatus.BAD_REQUEST);
	}	
	
	@PutMapping()
	public ResponseEntity<User> update(@RequestBody User user) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		if(user.getId()==null || user.getId().isEmpty()) {
			user = User.newBuilder(user).setId(loggedUserId).build();
		}
		if(user.getId()!=null && !user.getId().isEmpty() && !loggedUserId.equals(user.getId())) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		User updatedUser = this.userService.saveOrUpdate(user);
		if(updatedUser!=null) {
			return ResponseEntity.ok(updatedUser);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<User> findOne(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		User user = this.userService.findOne(loggedUserId);
		if(user!=null) {			
			return ResponseEntity.ok(user);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping()
	public ResponseEntity<User> delete(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		User user = this.userService.delete(loggedUserId);
		if(user!=null) {
			return ResponseEntity.ok(user);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping("/loyaltyProgram")
	public ResponseEntity<User> joinLoyaltyProgram(@RequestBody User user) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		if(!loggedUserId.equals(user.getId())) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		User updatedUser = this.userService.saveOrUpdate(user);
		if(updatedUser!=null) {
			return ResponseEntity.ok(updatedUser);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
}
