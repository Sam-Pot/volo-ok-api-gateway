package com.volook.apiGateway.schedulers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.volook.apiGateway.ticketBookingManagement.services.BookingService;
import com.volook.apiGateway.ticketBookingManagement.services.EmailService;
import com.volook.apiGateway.userManagement.services.UserService;

import emailManager.Email.EmailDto;
import ticketBookingManager.TicketBooking.Booking;
import ticketBookingManager.TicketBooking.BookingState;
import userManager.UserOuterClass.User;


@Service
public class BookingSchedulerService {
	@Autowired
	private BookingService bookingService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;
	
	private final String CRON_EXPRESSION = "0 1 * * *";
	private long todayMillis = new Date().getTime();
	private long tomorrowMillis = new Date().getTime() + (24*60*60*1000);
	
	private final String URL = "http://localhost:8004/scheduler";
	private final String FILTERS = "filter.state=$eq:"+BookingState.OPEN+"&filter.expirationDate=$btw:"+todayMillis+","+tomorrowMillis;
	private final String QUERY = URL+"?"+FILTERS;
		
	@Scheduled(cron = CRON_EXPRESSION)
	private void checkBookings() {
		try {
			List<Booking> paginatedBookings = this.bookingService.find(QUERY).getBookingsList();
			for(Booking b: paginatedBookings) {
				User user = this.userService.findOne(b.getUserId());
				if(user!=null) {
					String userEmail = user.getEmail();
					if(userEmail!=null) {
						EmailDto emailDto = EmailDto.newBuilder()
								.setTo(userEmail)
								.setSubject(ExpiringBookingEmailTemplate.SUBJECT)
								.setText(ExpiringBookingEmailTemplate.TEXT)
								.setHtml(ExpiringBookingEmailTemplate.HTML)
								.build();
						this.emailService.sendEmail(emailDto);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
