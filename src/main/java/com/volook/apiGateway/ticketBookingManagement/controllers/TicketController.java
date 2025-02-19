package com.volook.apiGateway.ticketBookingManagement.controllers;

import java.util.Date;

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
import com.volook.apiGateway.ticketBookingManagement.dto.BooleanDto;
import com.volook.apiGateway.ticketBookingManagement.dto.BuyTicketDto;
import com.volook.apiGateway.ticketBookingManagement.services.EmailService;
import com.volook.apiGateway.ticketBookingManagement.services.PaymentService;
import com.volook.apiGateway.ticketBookingManagement.services.TicketService;
import com.volook.apiGateway.userManagement.services.UserService;

import flightsManager.Flights.Fare;
import paymentManager.Payment.BillingInformations;
import ticketBookingManager.TicketBooking.EmailDto;
import ticketBookingManager.TicketBooking.PaginatedTickets;
import ticketBookingManager.TicketBooking.Ticket;
import ticketBookingManager.TicketBooking.TicketDto;
import ticketBookingManager.TicketBooking.TicketState;
import userManager.UserOuterClass.User;

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
	@Autowired
	private UserService userService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Ticket> findOne(@PathVariable("id") String ticketId){		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		Ticket ticket = this.ticketService.findOne(ticketId, loggedUserId, null);
		if(ticket!=null) {
			return ResponseEntity.ok(ticket);
		}
		return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
	}
	
	/*@GetMapping()
	public ResponseEntity<PaginatedTickets> find(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		String query = "userId="+loggedUserId;
		PaginatedTickets tickets = this.ticketService.find(query);
		if(tickets!=null) {
			return ResponseEntity.ok(tickets);
		}
		return new ResponseEntity<PaginatedTickets>(HttpStatus.BAD_REQUEST);
	}*/
	
	@GetMapping()
	public ResponseEntity<PaginatedTickets> findAllByUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		PaginatedTickets tickets = this.ticketService.findAllByUser(loggedUserId);
		if(tickets!=null) {
			return ResponseEntity.ok(tickets);
		}
		return new ResponseEntity<PaginatedTickets>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping()
	public ResponseEntity<Ticket> update(@RequestBody BuyTicketDto buyTicketDto) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		//CHECK IF TICKET IS EDITABLE
		Ticket ticket = Ticket.newBuilder()
				.setPassengerName( buyTicketDto.ticket().passengerName())
				.setPassengerSurname( buyTicketDto.ticket().passengerSurname())
				.setFareId( buyTicketDto.ticket().fareId())
				.setCustomerCode( buyTicketDto.ticket().customerCode())
				.setPrice( buyTicketDto.ticket().price())
				.setGeneratedPoints( buyTicketDto.ticket().generatedPoints())
				.setUsedPoints( buyTicketDto.ticket().usedPoints())
				.setFlightId( buyTicketDto.ticket().flightId())
				.setFlightDate( buyTicketDto.ticket().flightDate())
				.setBookingId( buyTicketDto.ticket().bookingId())
				.setFrom( buyTicketDto.ticket().from())
				.setTo( buyTicketDto.ticket().to()).build();
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
			if(updatedTicket==null) {
				return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
			}
			return ResponseEntity.ok(updatedTicket);
		}
		//UPDATE TICKET
		Ticket updatedTicket = this.ticketService.saveOrUpdate(ticket);
		if(updatedTicket==null) {
			return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
		}
		//ADDING PRICE
		BillingInformations billingInformations = BillingInformations.newBuilder()
				.setCardHolderName(buyTicketDto.billingInformation().cardHolderName())
				.setCardHolderSurname(buyTicketDto.billingInformation().cardHolderSurname())
				.setExpiryMonth(buyTicketDto.billingInformation().expiryMonth())
				.setExpiryYear(buyTicketDto.billingInformation().expiryYear())
				.setCvv(buyTicketDto.billingInformation().cvv())
				.setPAN(buyTicketDto.billingInformation().PAN())
				.setCost(updatingPrice)
				.build();
		//PROCESS PAYMENT
		boolean paymentSuccess = this.paymentService.pay(billingInformations);
		if(!paymentSuccess) {
			return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
		}
		//GENERATE NEW TICKET
		User user = this.userService.findOne(loggedUserId);
		if(user==null) {
			return new ResponseEntity<Ticket>(HttpStatus.BAD_REQUEST);
		}
		String userEmail = user.getEmail();
		TicketDto ticketDto = TicketDto.newBuilder()
				.setUserEmail(userEmail)
				.setTicketId(ticket.getId())
				.build();
		EmailDto emailDto =  this.ticketService.generateTicket(ticketDto);
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
	public ResponseEntity<BooleanDto> buy(@RequestBody BuyTicketDto buyTicketDto){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		try {
			//FILL TICKET PARAMETERS
			Ticket ticketToBuy = Ticket.newBuilder()
					.setId(buyTicketDto.ticket().id()!=null?buyTicketDto.ticket().id():"")
					.setPassengerName( buyTicketDto.ticket().passengerName())
					.setPassengerSurname( buyTicketDto.ticket().passengerSurname())
					.setFareId( buyTicketDto.ticket().fareId())
					.setCustomerCode( buyTicketDto.ticket().customerCode()!=null?buyTicketDto.ticket().customerCode():"")
					.setPrice( buyTicketDto.ticket().price())
					.setGeneratedPoints( buyTicketDto.ticket().generatedPoints())
					.setUsedPoints( buyTicketDto.ticket().usedPoints())
					.setFlightId( buyTicketDto.ticket().flightId())
					.setFlightDate( (new Date(buyTicketDto.ticket().flightDate()).getTime()))
					.setBookingId( buyTicketDto.ticket().bookingId()==null?"":buyTicketDto.ticket().bookingId())
					.setFrom( buyTicketDto.ticket().from())
					.setTo( buyTicketDto.ticket().to()).build();
			if(ticketToBuy==null) {
				return new ResponseEntity<BooleanDto>(HttpStatus.BAD_REQUEST);
			}
			float ticketPrice = this.ticketService.calculatePrice(ticketToBuy);//FareCost * KM
			if(ticketPrice<0) {
				return new ResponseEntity<BooleanDto>(HttpStatus.BAD_REQUEST);
			}
			int generatedPoints = 0;
			//ADD AND USE POINTS (ONLY LOYALTY_USER)
			if(ticketToBuy.getCustomerCode()!=null && !ticketToBuy.getCustomerCode().isEmpty()) {
				generatedPoints = this.ticketService.calculateGeneratedPoints(ticketToBuy.getFlightId());
				if(generatedPoints==-1) {
					return new ResponseEntity<BooleanDto>(HttpStatus.BAD_REQUEST);
				}
				int addedPoints = this.userService.addPoints(loggedUserId, ticketToBuy.getCustomerCode(), generatedPoints);
				int usedPoints = this.userService.usePoints(loggedUserId, ticketToBuy.getCustomerCode(),ticketToBuy.getUsedPoints());
				ticketPrice -= usedPoints;
			}
			//SAVE TICKET TO DB
			Ticket ticketToSave = Ticket.newBuilder(ticketToBuy)
					.setGeneratedPoints(generatedPoints)
					.setUserId(loggedUserId)
					.setPrice(ticketPrice)
					.setFlightDate(buyTicketDto.ticket().flightDate())
					.setState(TicketState.PURCHASED)
					.build();
			Ticket savedTicket = this.ticketService.saveOrUpdate(ticketToSave);
			//PROCESS PAYMENT	
			BillingInformations billingInformations = BillingInformations.newBuilder()
					.setCardHolderName(buyTicketDto.billingInformation().cardHolderName())
					.setCardHolderSurname(buyTicketDto.billingInformation().cardHolderSurname())
					.setExpiryMonth(buyTicketDto.billingInformation().expiryMonth())
					.setExpiryYear(buyTicketDto.billingInformation().expiryYear())
					.setCvv(buyTicketDto.billingInformation().cvv())
					.setPAN(buyTicketDto.billingInformation().PAN())
					.setCost(ticketPrice)
					.build();
			boolean paymentOk = this.paymentService.pay(billingInformations);
			if(!paymentOk) {
				this.ticketService.delete(savedTicket.getId());
				return new ResponseEntity<BooleanDto>(HttpStatus.BAD_REQUEST);
			}
			//UPDATE LAST PURCHASE DATE IN USER
			boolean dateUpdated = this.userService.setLastPurchaseDate((new Date()).getTime(), loggedUserId);
			if(!dateUpdated) {
				return new ResponseEntity<BooleanDto>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			//GENERATE TICKET
			User user = this.userService.findOne(loggedUserId);
			if(user==null) {
				return new ResponseEntity<BooleanDto>(HttpStatus.BAD_REQUEST);
			}
			String userEmail = user.getEmail();
			TicketDto ticketDto = TicketDto.newBuilder()
					.setUserEmail(userEmail)
					.setTicketId(savedTicket.getId())
					.build();
			
			EmailDto emailDto =  this.ticketService.generateTicket(ticketDto);
			if(emailDto==null) {
				return new ResponseEntity<BooleanDto>(HttpStatus.BAD_REQUEST);
			}
			//SEND TICKET BY EMAIL
			emailManager.Email.EmailDto emailTemplate = emailManager.Email.EmailDto.newBuilder()
					.setTo(emailDto.getTo())
					.setSubject(emailDto.getSubject())
					.setText(emailDto.getText())
					.setHtml(emailDto.getHtml())
					.build();
			Boolean emailSent = this.emailService.sendEmail(emailTemplate);
			if(emailSent) {
				return ResponseEntity.ok(new BooleanDto(emailSent));
			}
			return new ResponseEntity<BooleanDto>(HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			return new ResponseEntity<BooleanDto>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/sendTicket/{ticketId}")
	public ResponseEntity<Boolean> resendTicket(@PathVariable("ticketId") String ticketId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedUserId = (String) auth.getPrincipal();
		User user = this.userService.findOne(loggedUserId);
		if(user==null) {
			return new ResponseEntity<Boolean>(HttpStatus.BAD_REQUEST);
		}
		String userEmail = user.getEmail();
		TicketDto ticketDto = TicketDto.newBuilder()
				.setUserEmail(userEmail)
				.setTicketId(ticketId)
				.build();
		
		EmailDto emailDto =  this.ticketService.generateTicket(ticketDto);
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
	} 
	
}
