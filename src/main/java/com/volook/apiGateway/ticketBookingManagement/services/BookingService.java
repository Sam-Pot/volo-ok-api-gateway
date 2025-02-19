package com.volook.apiGateway.ticketBookingManagement.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import net.devh.boot.grpc.client.inject.GrpcClient;
import ticketBookingManager.BookingServiceGrpc.BookingServiceBlockingStub;
import ticketBookingManager.TicketBooking.Booking;
import ticketBookingManager.TicketBooking.BookingDto;
import ticketBookingManager.TicketBooking.PaginatedBookings;
import ticketBookingManager.TicketBooking.Ticket;
import userManager.UserOuterClass.User;
import userManager.UserOuterClass.UserId;
import userManager.UserServiceGrpc.UserServiceBlockingStub;
import ticketBookingManager.TicketBooking.PaginateQueryDto;

@Service
public class BookingService {
	
	@GrpcClient(Microservice.TICKET_BOOKING_MANAGER)
	private BookingServiceBlockingStub bookingService;
	@GrpcClient(Microservice.USER_MANAGER)
	private UserServiceBlockingStub userService;
	
	public Booking findOne(String bookingId, String userId) {
		if(bookingId==null) {
			return null;
		}
		BookingDto bookingDto = BookingDto.newBuilder()
				.setUserId(userId)
				.setBookingId(bookingId)
				.build();
		Booking booking = this.bookingService.findOne(bookingDto);
		return booking;
	}
	
	public PaginatedBookings find(String query) {
		if(query==null) {
			return null;
		}
		PaginateQueryDto paginateQuery = PaginateQueryDto.newBuilder()
				.setQuery(query)
				.build();
		PaginatedBookings bookings = this.bookingService.find(paginateQuery);
		return bookings;
	}
	
	public Booking saveOrUpdate(Booking booking) {
		if(booking==null) {
			return null;
		}
		UserId userId = UserId.newBuilder().setId(booking.getUserId()).build();
		User user = this.userService.findOne(userId);
		Booking bookingToSave = booking;
		System.out.println(user.getCustomerCode());
		if(user.getCustomerCode()!=null && !user.getCustomerCode().isEmpty()) {
			List<Ticket> tickets = booking.getTicketsList();
			List<Ticket> ticketsToSave = new ArrayList<Ticket>();
			for(Ticket t: tickets) {
				Ticket curTicket = Ticket.newBuilder(t).setCustomerCode(user.getCustomerCode()).build();
				ticketsToSave.add(curTicket);
			}
			bookingToSave = Booking.newBuilder(booking).clearTickets().addAllTickets(ticketsToSave).build();
		}
		Booking savedBooking = this.bookingService.saveOrUpdate(bookingToSave);
		return savedBooking;
	}
	
	public Booking delete(BookingDto bookingDto) {
		if(bookingDto==null) {
			return null;
		}
		return this.bookingService.delete(bookingDto);
	}
}
