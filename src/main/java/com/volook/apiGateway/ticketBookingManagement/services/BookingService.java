package com.volook.apiGateway.ticketBookingManagement.services;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import net.devh.boot.grpc.client.inject.GrpcClient;
import ticketBookingManager.BookingServiceGrpc.BookingServiceBlockingStub;
import ticketBookingManager.TicketBooking.Booking;
import ticketBookingManager.TicketBooking.BookingDto;
import ticketBookingManager.TicketBooking.PaginatedBookings;
import ticketBookingManager.TicketBooking.PaginateQueryDto;

@Service
public class BookingService {
	
	@GrpcClient(Microservice.TICKET_BOOKING_MANAGER)
	private BookingServiceBlockingStub bookingService;
	
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
		Booking savedBooking = this.bookingService.saveOrUpdate(booking);
		return savedBooking;
	}
	
	public Booking delete(BookingDto bookingDto) {
		if(bookingDto==null) {
			return null;
		}
		return this.bookingService.delete(bookingDto);
	}
}
