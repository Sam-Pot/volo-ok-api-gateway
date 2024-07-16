package com.volook.apiGateway.schedulers;

import java.util.LinkedList;
import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.volook.apiGateway.flightsManagement.services.PromotionService;
import com.volook.apiGateway.ticketBookingManagement.services.EmailService;
import com.volook.apiGateway.userManagement.services.UserService;

import flightsManager.Flights.Promotion;
import userManager.UserOuterClass.PaginatedUsers;
import userManager.UserOuterClass.User;

@Service
public class PromotionSchedulerService {
	@Autowired
	private PromotionService promotionService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserService userService;
	
	private final String CRON_EXPRESSION = "0 0 * * 0";//every sunday night
	
	private final String URL = "http://localhost:8004/scheduler";
	private final String FILTERS = "filter.customerCode=$not:$null";
	private final String QUERY = URL+"?"+FILTERS;

	@Scheduled(cron = CRON_EXPRESSION)
	private void fetchPromotions() {
		//GET PROMOTIONS FOR LOYALTY CUSTOMER
		List<Promotion> promotions = this.promotionService.getLoyaltyCustomerPromotions();
		//GET LOYALTY CUSTOMERS' EMAIL
		PaginatedUsers paginatedUsers = this.userService.find(QUERY);
		List<String> loyaltyUsersEmail = new LinkedList<>();
		if(promotions!= null && paginatedUsers!=null) {
			for(User user: paginatedUsers.getUsersList()) {
				loyaltyUsersEmail.add(user.getEmail());
			}
			for(Promotion p: promotions) {
				String html = "<p>E' disponibile una nuova promozione dal "+ new Date(p.getStartDate())+ " al" +
						new Date(p.getEndDate())+"del "+p.getDiscountPercentage()*100 +"%"+"</p>";
				for(String email: loyaltyUsersEmail) {
					emailManager.Email.EmailDto emailTemplate = emailManager.Email.EmailDto.newBuilder()
							.setTo(email)
							.setText(PromotionAdsEmailTemplate.TEXT)
							.setSubject(PromotionAdsEmailTemplate.SUBJECT)
							.setHtml(html)
							.build();
					this.emailService.sendEmail(emailTemplate);
				}
			}
		}
	}
}
