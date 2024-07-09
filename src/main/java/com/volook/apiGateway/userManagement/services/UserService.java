package com.volook.apiGateway.userManagement.services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import net.devh.boot.grpc.client.inject.GrpcClient;
import userManager.UserOuterClass.EmailAddress;
import userManager.UserOuterClass.PaginateQuery;
import userManager.UserOuterClass.PaginatedUsers;
import userManager.UserOuterClass.User;
import userManager.UserOuterClass.UserId;
import userManager.UserServiceGrpc.UserServiceBlockingStub;

@Service
public class UserService {
	@GrpcClient(Microservice.USER_MANAGER)
	private UserServiceBlockingStub userServiceStub;
	
	public User findOneByEmail(String emailAddress) {
		if(emailAddress==null) {
			return null;
		}
		EmailAddress userEmailReq = EmailAddress.newBuilder()
				.setEmailAddress(emailAddress)
		        .build();
		User user = this.userServiceStub.findOneByEmail(userEmailReq);
		user = User.newBuilder(user)
				.setSaltedPassword(null)
				.build();
		return user;
	}
	
	public User findOne(String userId) {
		if(userId==null) {
			return null;
		}
		UserId userIdReq = UserId.newBuilder()
				.setId(userId)
		        .build();
		User user = this.userServiceStub.findOne(userIdReq);
		user = User.newBuilder(user)
				.setSaltedPassword(null)
				.build();
		return user;
	}
	
	public boolean checkCredentials(String emailAddress, String password) {
		if(emailAddress==null || password==null) {
			return false;
		}
		User user = this.findOneByEmail(emailAddress);
		if(user==null) {
			return false;
		}
		if(BCrypt.checkpw(password,user.getSaltedPassword())){
			return true;
		}
		return false;
	}
	
	public User saveOrUpdate(User user) {
		if(user==null || user.getEmail()==null || user.getRole()==null) {
			return null;
		}
		User userToSave = user;
		if(user.getSaltedPassword()!=null) {
			//ENCRYPT AND SALT PASSWORD
			String saltedPassword = BCrypt.hashpw(user.getSaltedPassword(), BCrypt.gensalt());
			userToSave = User.newBuilder(user).setSaltedPassword(saltedPassword).build();
		}
		User savedUser = this.userServiceStub.saveOrUpdate(userToSave);
		savedUser = User.newBuilder(savedUser)
				.setSaltedPassword(null)
				.build();
		return savedUser;
	}
	
	public User delete(String userId) {
		if(userId==null) {
			return null;
		}
		UserId id = UserId.newBuilder()
				.setId(userId)
				.build();
		User userToDelete = this.userServiceStub.clearData(id);
		userToDelete = User.newBuilder(userToDelete)
				.setSaltedPassword(null)
				.build();
		return userToDelete;
	}
	
	public PaginatedUsers find(String query){
		PaginateQuery paginateQuery = PaginateQuery.newBuilder()
				.setQuery(query)
				.build();
		PaginatedUsers paginatedUsers = this.userServiceStub.find(paginateQuery);
		if(paginatedUsers==null) {
			return null;
		}
		return paginatedUsers;//SALTED PASSWORD IS RETURNED ALREADY EMPTY
	}
}
