package com.volook.apiGateway.ticketBookingManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.volook.apiGateway.flightsManagement.services.FareService;
import com.volook.apiGateway.ticketBookingManagement.dto.BuyTicketDto;
import com.volook.apiGateway.ticketBookingManagement.services.EmailService;
import com.volook.apiGateway.ticketBookingManagement.services.PaymentService;
import com.volook.apiGateway.ticketBookingManagement.services.TicketService;

import flightsManager.Flights.Fare;
import paymentManager.Payment.BillingInformations;
import ticketBookingManager.TicketBooking.EmailDto;
import ticketBookingManager.TicketBooking.PaginatedFidelityStatistics;
import ticketBookingManager.TicketBooking.PaginatedTickets;
import ticketBookingManager.TicketBooking.Ticket;

@RestController
@RequestMapping("/tickets")
public class TicketController {
	@Autowired
	private TicketService ticketService;
	@Autowired
	private FareService fareService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Ticket> findOne(@PathVariable("id") String ticketId){		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		Ticket ticket = this.ticketService.findOne(ticketId,loggedUserId);
		if(ticket!=null) {
			return ResponseEntity.ok(ticket);
		}
		return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedTickets> find(@RequestParam String query){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		query += "&userId="+loggedUserId;
		PaginatedTickets tickets = this.ticketService.find(query);
		if(tickets!=null) {
			return ResponseEntity.ok(tickets);
		}
		return new ResponseEntity<PaginatedTickets>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping()
	public ResponseEntity<Ticket> update(@RequestBody BuyTicketDto buyTicketDto) {
		//CHECK IF TICKET IS EDITABLE
		Ticket ticket = buyTicketDto.ticket();
		Fare ticketFare = this.fareService.findOne(ticket.getFareId());
		if(ticketFare==null) {
			return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
		}
		boolean isTicketEditable = ticketFare.getEditable();
		if(!isTicketEditable) {
			return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
		}
		//CHECK UPDATING PRICE
		float updatingPrice = ticketFare.getModificationPrice();
		//NO ADDING PRICE
		if(updatingPrice==0) {
			Ticket updatedTicket = this.ticketService.saveOrUpdate(ticket);
			return ResponseEntity.ok(updatedTicket);
		}
		//UPDATE TICKET
		Ticket updatedTicket = this.ticketService.saveOrUpdate(ticket);
		//ADDING PRICE
		BillingInformations billingInformations = BillingInformations.newBuilder(buyTicketDto.billingInformation())
				.setCost(updatingPrice)
				.build();
		//PROCESS PAYMENT
		boolean paymentSuccess = this.paymentService.pay(billingInformations);
		if(!paymentSuccess) {
			return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
		}
		//GENERATE TICKET
		EmailDto emailDto =  this.ticketService.generateTicket(ticket.getId());
		if(emailDto==null) {
			return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
		}
		//SEND TICKET BY EMAIL
		emailManager.Email.EmailDto emailTemplate = emailManager.Email.EmailDto.newBuilder()
				.setTo(emailDto.getTo())
				.setSubject(emailDto.getSubject())
				.setText(emailDto.getText())
				.setHtml(emailDto.getHtml())
				.build();
		boolean emailSent = this.emailService.sendEmail(emailTemplate);
		if(emailSent) {
			return ResponseEntity.ok(updatedTicket);
		}
		return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/payments")
	public ResponseEntity<Boolean> buy(@RequestBody BuyTicketDto buyTicketDto){
		try {
			//SAVE TICKET IN DB
			Ticket ticket = this.ticketService.saveOrUpdate(buyTicketDto.ticket());
			if(ticket==null) {
				return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
			}
			//PROCESS PAYMENT
			boolean paymentOk = this.paymentService.pay(buyTicketDto.billingInformation());
			if(!paymentOk) {
				return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
			}
			//GENERATE TICKET
			EmailDto emailDto =  this.ticketService.generateTicket(ticket.getId());
			if(emailDto==null) {
				return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
			}
			//SEND TICKET BY EMAIL
			emailManager.Email.EmailDto emailTemplate = emailManager.Email.EmailDto.newBuilder()
					.setTo(emailDto.getTo())
					.setSubject(emailDto.getSubject())
					.setText(emailDto.getText())
					.setHtml(emailDto.getHtml())
					.build();
			boolean emailSent = this.emailService.sendEmail(emailTemplate);
			if(emailSent) {
				return ResponseEntity.ok(emailSent);
			}
			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/statistics/{id}")
	public ResponseEntity<PaginatedFidelityStatistics> viewPointsBalance(@PathVariable String userId){
		PaginatedFidelityStatistics statistics = this.ticketService.getFidelityStatistics(userId);
		if(statistics!=null) {
			return ResponseEntity.ok(statistics);
		}
		return new ResponseEntity<PaginatedFidelityStatistics>(HttpStatus.BAD_REQUEST);
	}
	
}
