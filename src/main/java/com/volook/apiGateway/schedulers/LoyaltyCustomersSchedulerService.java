package com.volook.apiGateway.schedulers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.volook.apiGateway.ticketBookingManagement.services.EmailService;
import com.volook.apiGateway.userManagement.services.UserService;

import userManager.UserOuterClass.EmailDto;
import userManager.UserOuterClass.Years;


@Service
public class LoyaltyCustomersSchedulerService {
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;
	private final int YEARS_LIMIT = 2;//if a customer doesn't buy a ticket for 2 years is considered unfaithful
	private final String CRON_EXPRESSION = "0 1 * * *";
	Years years = Years.newBuilder()
			.setYear(YEARS_LIMIT)
			.build();
	
	@Scheduled(cron = CRON_EXPRESSION )
	private void loyaltyCheckerScheduler() {
		try {
			List<EmailDto> emailsToSend = this.userService.getUnfaithfulCustomers(years);
			if(emailsToSend==null) {
				return;
			}
			for(EmailDto emailDto: emailsToSend) {
				emailManager.Email.EmailDto email = emailManager.Email.EmailDto.newBuilder()
						.setTo(emailDto.getTo())
						.setText(emailDto.getText())
						.setSubject(emailDto.getSubject())
						.setHtml(emailDto.getHtml())
						.build();
				this.emailService.sendEmail(email);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
