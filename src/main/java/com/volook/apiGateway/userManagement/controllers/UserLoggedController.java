package com.volook.apiGateway.userManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volook.apiGateway.auth.JwtService;
import com.volook.apiGateway.userManagement.dto.LoginDto;

@RestController
@RequestMapping("user")
public class UserLoggedController {
	@Autowired
	private JwtService jwtService;
	
	
	/*@PostMapping("/{id}")
		public ResponseEntity<?> signUp(@PathVariable(name = "id") String id) {
			return ResponseEntity.status(HttpStatus.OK).build();
	 }*/
	
	@PostMapping("/login")
	  public ResponseEntity<String> signIn(@RequestBody LoginDto data) {
	    String accessToken = jwtService.generateJwtToken(data.emailAddress(), data.password());
	    return ResponseEntity.ok(accessToken);
	  }
}
