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

import com.volook.apiGateway.auth.JwtService;
import com.volook.apiGateway.userManagement.dto.LoginDto;
import com.volook.apiGateway.userManagement.services.UserService;

import userManager.UserOuterClass.User;

@RestController
@RequestMapping("user")
public class UserLoggedController {
	
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginDto data) {
		String accessToken = jwtService.generateJwtToken(data.emailAddress(), data.password());
		if(accessToken != null) {
			return ResponseEntity.ok(accessToken);
		}
		return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<String> signin(@RequestBody LoginDto data) {
		User userDto = User.newBuilder()
				.setEmail(data.emailAddress())
				.setSaltedPassword(data.password())
				.build();
		User user = this.userService.saveOrUpdate(userDto);
		if(user!=null) {
			String accessToken = this.jwtService.generateJwtToken(data.emailAddress(), data.password());
			return ResponseEntity.ok(accessToken);
		}
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
	}	
	
	@PutMapping()
	public ResponseEntity<User> update(@RequestBody User user) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserEmail = (String) auth.getPrincipal();
		if(!loggedUserEmail.equals(user.getEmail())) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		User updatedUser = this.userService.saveOrUpdate(user);
		if(updatedUser!=null) {
			return ResponseEntity.ok(updatedUser);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/{email}")
	public ResponseEntity<User> findOne(@PathVariable("email") String email){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserEmail = (String) auth.getPrincipal();
		if(!loggedUserEmail.equals(email)) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		User user = this.userService.findOne(email);
		if(user!=null) {
			return ResponseEntity.ok(user);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/{email}")
	public ResponseEntity<User> delete(@PathVariable("email") String email){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserEmail = (String) auth.getPrincipal();
		if(!loggedUserEmail.equals(email)) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		User user = this.userService.delete(email);
		if(user!=null) {
			return ResponseEntity.ok(user);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping("/loyaltyProgram")
	public ResponseEntity<User> joinLoyaltyProgram(@RequestBody User user) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserEmail = (String) auth.getPrincipal();
		if(!loggedUserEmail.equals(user.getEmail())) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		User updatedUser = this.userService.saveOrUpdate(user);
		if(updatedUser!=null) {
			return ResponseEntity.ok(updatedUser);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
}
