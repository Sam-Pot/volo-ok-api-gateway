package com.volook.apiGateway.userManagement.services;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import net.devh.boot.grpc.client.inject.GrpcClient;
import userManager.UserOuterClass.DateUpdated;
import userManager.UserOuterClass.EmailAddress;
import userManager.UserOuterClass.EmailDto;
import userManager.UserOuterClass.EmailList;
import userManager.UserOuterClass.LastPurchaseDto;
import userManager.UserOuterClass.PaginateQueryDto;
import userManager.UserOuterClass.PaginatedUsers;
import userManager.UserOuterClass.PointsData;
import userManager.UserOuterClass.PointsResponse;
import userManager.UserOuterClass.User;
import userManager.UserOuterClass.UserId;
import userManager.UserOuterClass.Years;
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
				.setSaltedPassword("")
				.setBirthDate(user.getBirthDate())
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
		if(user.getSaltedPassword()!=null && !user.getSaltedPassword().isEmpty()) {
			//ENCRYPT AND SALT PASSWORD
			String saltedPassword = BCrypt.hashpw(user.getSaltedPassword(), BCrypt.gensalt());
			userToSave = User.newBuilder(user).setSaltedPassword(saltedPassword).build();
		}
		User savedUser = this.userServiceStub.saveOrUpdate(userToSave);
		savedUser = User.newBuilder(savedUser)
				.setSaltedPassword("")
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
				.setSaltedPassword("")
				.build();
		return userToDelete;
	}
	
	public PaginatedUsers find(String query){
		PaginateQueryDto paginateQuery = PaginateQueryDto.newBuilder()
				.setQuery(query)
				.build();
		PaginatedUsers paginatedUsers = this.userServiceStub.find(paginateQuery);
		System.out.println(paginatedUsers);
		if(paginatedUsers==null) {
			return null;
		}
		return paginatedUsers;//SALTED PASSWORD IS RETURNED ALREADY EMPTY
	}
	
	public int addPoints(String userId, String customerCode, int points) {
		if(userId==null || points <= 0 || customerCode==null) {
			return 0;
		}
		PointsData pointsData = PointsData.newBuilder()
				.setUserId(userId)
				.setPoints(points)
				.setCustomerCode(customerCode)
				.build();
		PointsResponse addedPoints = this.userServiceStub.addPoints(pointsData);
		return addedPoints.getPoints();
	}
	
	public int usePoints(String userId, String customerCode, int points) {
		if(userId==null || points <= 0 || customerCode==null) {
			return 0;
		}
		PointsData pointsData = PointsData.newBuilder()
				.setUserId(userId)
				.setPoints(points)
				.setCustomerCode(customerCode)
				.build();
		PointsResponse usedPoints = this.userServiceStub.usePoints(pointsData);
		return usedPoints.getPoints();
	}
	
	public boolean setLastPurchaseDate(long dateMillis, String userId) {
		LastPurchaseDto dto = LastPurchaseDto.newBuilder()
				.setDate(dateMillis)
				.setUserId(userId)
				.build();
		DateUpdated updated = this.userServiceStub.setLastPurchaseDate(dto);
		return updated.getUpdated();
	}
	
	public List<EmailDto> getUnfaithfulCustomers(Years years){
		EmailList emailList = this.userServiceStub.checkLoyalty(years);
		if(emailList==null) {
			return null;
		}
		List<EmailDto> emailTemplates = emailList.getEmailListList();
		return emailTemplates;
	}
}
