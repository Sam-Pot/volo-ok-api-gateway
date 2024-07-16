package com.volook.apiGateway.userManagement.controllers;

import java.util.Map;

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
	
	@GetMapping("/{id}")
	public ResponseEntity<User> findOne(@PathVariable("id") String userId){
		User user = this.userService.findOne(userId);
		if(user!=null) {
			return ResponseEntity.ok(user);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedUsers> find(@RequestParam Map<String,String> mapQuery){
		String query = "";
		for(String s: mapQuery.keySet()) {
			query+=s+"="+mapQuery.get(s);
		}
		PaginatedUsers users = this.userService.find(query);
		if(users!=null) {
			return ResponseEntity.ok(users);
		}
		return new ResponseEntity<PaginatedUsers>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<User> delete(@PathVariable("id") String userId){
		User user = this.userService.delete(userId);
		if(user!=null) {
			return ResponseEntity.ok(user);
		}
		return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
	}
	
}


