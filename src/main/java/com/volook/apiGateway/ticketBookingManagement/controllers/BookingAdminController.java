package com.volook.apiGateway.ticketBookingManagement.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.volook.apiGateway.ticketBookingManagement.services.BookingService;
import ticketBookingManager.TicketBooking.Booking;
import ticketBookingManager.TicketBooking.PaginatedBookings;

@RestController
@RequestMapping("admin/bookings")
public class BookingAdminController {
	@Autowired
	private BookingService bookingService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Booking> findOne(@PathVariable("id") String bookingId){		
		Booking booking = this.bookingService.findOne(bookingId,null);
		if(booking!=null) {
			return ResponseEntity.ok(booking);
		}
		return new ResponseEntity<Booking>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedBookings> find(@RequestParam String query){
		PaginatedBookings bookings = this.bookingService.find(query);
		if(bookings!=null) {
			return ResponseEntity.ok(bookings);
		}
		return new ResponseEntity<PaginatedBookings>(HttpStatus.BAD_REQUEST);
	}
}
