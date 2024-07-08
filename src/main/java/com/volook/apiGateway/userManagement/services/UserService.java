package com.volook.apiGateway.userManagement.services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import net.devh.boot.grpc.client.inject.GrpcClient;
import userManager.UserOuterClass.EmailAddress;
import userManager.UserOuterClass.User;
import userManager.UserServiceGrpc.UserServiceBlockingStub;

@Service
public class UserService {
	@GrpcClient(Microservice.USER_MANAGER)
	private UserServiceBlockingStub userServiceStub;
	
	public User findOne(String emailAddress) {
		if(emailAddress==null) {
			return null;
		}
		EmailAddress userEmailReq = EmailAddress.newBuilder()
				.setEmail(emailAddress)
		        .build();
		User user = this.userServiceStub.findOne(userEmailReq);
		return user;
	}
	
	public boolean checkCredentials(String emailAddress, String password) {
		if(emailAddress==null || password==null) {
			return false;
		}
		User user = this.findOne(emailAddress);
		if(BCrypt.checkpw(password,user.getSaltedPassword())){
			return true;
		}
		return false;
	}
	
	public User save(User user) {
		if(user==null || user.getEmail()==null || user.getRole()==null) {
			return null;
		}
		User userToSave = user;
		if(user.getSaltedPassword()!=null) {
			//ENCRYPT AND SALT PASSWORD
			String saltedPassword = BCrypt.hashpw(user.getSaltedPassword(), BCrypt.gensalt());
			userToSave = User.newBuilder(user).setSaltedPassword(saltedPassword).build();
		}
		return this.userServiceStub.saveOrUpdate(userToSave);
	}
}
