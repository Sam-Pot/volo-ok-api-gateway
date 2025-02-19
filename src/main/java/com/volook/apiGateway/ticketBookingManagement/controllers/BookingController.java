package com.volook.apiGateway.ticketBookingManagement.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.volook.apiGateway.ticketBookingManagement.services.BookingService;
import ticketBookingManager.TicketBooking.Booking;
import ticketBookingManager.TicketBooking.BookingDto;
import ticketBookingManager.TicketBooking.BookingState;
import ticketBookingManager.TicketBooking.PaginatedBookings;
import ticketBookingManager.TicketBooking.Ticket;

@RestController
@RequestMapping("bookings")
public class BookingController {
	
	@Autowired
	private BookingService bookingService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Booking> findOne(@PathVariable("id") String bookingId){		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		Booking booking = this.bookingService.findOne(bookingId,loggedUserId);
		if(booking!=null) {
			return ResponseEntity.ok(booking);
		}
		return new ResponseEntity<Booking>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedBookings> find(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		String query = "userId="+loggedUserId;
		PaginatedBookings paginatedBookings = this.bookingService.find(query);
		if(paginatedBookings!=null) {
			return ResponseEntity.ok(paginatedBookings);
		}
		return new ResponseEntity<PaginatedBookings>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping()
	public ResponseEntity<Booking> update(@RequestBody Booking booking) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		Booking bookingToUpdate = Booking.newBuilder(booking)
				.setUserId(loggedUserId)
				.build();
		Booking updatedBooking = this.bookingService.saveOrUpdate(bookingToUpdate);
		if(updatedBooking!=null) {
			return ResponseEntity.ok(updatedBooking);
		}
		return new ResponseEntity<Booking>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping()
	public ResponseEntity<Booking> save(@RequestBody Booking booking) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		//Assuming tickets refers all to the same departure date
		//long expirationDate =tickets.get(0).getFlightDate() - (3*24*60*60*1000); //3 days before the flight
		Booking bookingToSave = Booking.newBuilder(booking)
				.setUserId(loggedUserId)
				//.setExpirationDate(expirationDate)
				.build();
		Booking savedBooking = this.bookingService.saveOrUpdate(bookingToSave);
		if(savedBooking!=null) {
			return ResponseEntity.ok(savedBooking);
		}
		return new ResponseEntity<Booking>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Booking> delete(@PathVariable("id") String bookingId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		BookingDto bookingDto = BookingDto.newBuilder()
				.setUserId(loggedUserId)
				.setBookingId(bookingId)
				.build();
		Booking bookingDeleted = this.bookingService.delete(bookingDto);
		if(bookingDeleted!=null) {
			return ResponseEntity.ok(bookingDeleted);
		}
		return new ResponseEntity<Booking>(HttpStatus.BAD_REQUEST);
	}
}
