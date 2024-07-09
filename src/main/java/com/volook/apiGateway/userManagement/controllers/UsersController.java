package com.volook.apiGateway.userManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.volook.apiGateway.userManagement.services.UserService;
import userManager.UserOuterClass.PaginatedUsers;
import userManager.UserOuterClass.User;

@RestController
@RequestMapping("admin/user")
public class UsersController {
	
	@Autowired
	private UserService userService;	
	
	@PutMapping()
	public ResponseEntity<User> update(@RequestBody User user) {
		User updatedUser = this.userService.saveOrUpdate(user);
		if(updatedUser!=null) {
			return ResponseEntity.ok(updatedUser);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/{email}")
	public ResponseEntity<User> findOne(@PathVariable("email") String email){
		User user = this.userService.findOne(email);
		if(user!=null) {
			return ResponseEntity.ok(user);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedUsers> find(@RequestParam String query){
		PaginatedUsers users = this.userService.find(query);
		if(users!=null) {
			return ResponseEntity.ok(users);
		}
		return new ResponseEntity<PaginatedUsers>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/{email}")
	public ResponseEntity<User> delete(@PathVariable("email") String email){
		User user = this.userService.delete(email);
		if(user!=null) {
			return ResponseEntity.ok(user);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
}


