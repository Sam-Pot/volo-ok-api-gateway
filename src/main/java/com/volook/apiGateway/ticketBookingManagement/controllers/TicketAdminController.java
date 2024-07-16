package com.volook.apiGateway.ticketBookingManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.volook.apiGateway.ticketBookingManagement.services.TicketService;

import ticketBookingManager.TicketBooking.PaginatedTickets;
import ticketBookingManager.TicketBooking.Ticket;
import ticketBookingManager.TicketBooking.TicketDto;

@RestController
@RequestMapping("admin/tickets")
public class TicketAdminController {
	
	@Autowired
	private TicketService ticketService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Ticket> findOne(@PathVariable("id") String ticketId){		
		Ticket ticket = this.ticketService.findOne(ticketId,null,null);
		if(ticket!=null) {
			return ResponseEntity.ok(ticket);
		}
		return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedTickets> find(@RequestParam String query){
		PaginatedTickets tickets = this.ticketService.find(query);
		if(tickets!=null) {
			return ResponseEntity.ok(tickets);
		}
		return new ResponseEntity<PaginatedTickets>(HttpStatus.BAD_REQUEST);
	}
}
