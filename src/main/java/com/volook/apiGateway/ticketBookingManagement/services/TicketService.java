package com.volook.apiGateway.ticketBookingManagement.services;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import flightsManager.FareServiceGrpc.FareServiceBlockingStub;
import flightsManager.FlightServiceGrpc.FlightServiceBlockingStub;
import flightsManager.Flights.Fare;
import flightsManager.Flights.Flight;
import flightsManager.Flights.IdDto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import ticketBookingManager.TicketBooking;
import ticketBookingManager.TicketBooking.EmailDto;
import ticketBookingManager.TicketBooking.PaginateQueryDto;
import ticketBookingManager.TicketBooking.PaginatedTickets;
import ticketBookingManager.TicketBooking.Ticket;
import ticketBookingManager.TicketBooking.TicketDto;
import ticketBookingManager.TicketServiceGrpc.TicketServiceBlockingStub;

@Service
public class TicketService {
	
	@GrpcClient(Microservice.TICKET_BOOKING_MANAGER)
	private TicketServiceBlockingStub ticketServiceStub;
	
	@GrpcClient(Microservice.FLIGHTS_MANAGER)
	private FlightServiceBlockingStub flightServiceStub;
	
	@GrpcClient(Microservice.FLIGHTS_MANAGER)
	private FareServiceBlockingStub fareServiceStub;
	
	
	public Ticket findOne(String ticketId, String userId, String userEmail) {
		if(ticketId==null) {
			return null;
		}
		TicketDto ticketDto = TicketDto.newBuilder()
				.setUserId(userId)
				.setTicketId(ticketId)
				.setUserEmail(userEmail!=null?userEmail:"")
				.build();
		Ticket ticket = this.ticketServiceStub.findOne(ticketDto);
		return ticket;
	}
	
	public PaginatedTickets find(String query) {
		if(query==null) {
			return null;
		}
		PaginateQueryDto paginateQuery = PaginateQueryDto.newBuilder()
				.setQuery(query)
				.build();
		PaginatedTickets tickets = this.ticketServiceStub.find(paginateQuery);
		return tickets;
	}
	
	public PaginatedTickets findAllByUser(String userId) {
		if(userId==null) {
			return null;
		}
		ticketBookingManager.TicketBooking.IdDto idDto = ticketBookingManager.TicketBooking.IdDto.newBuilder().setId(userId).build();
		PaginatedTickets tickets = this.ticketServiceStub.findAllByUser(idDto);
		return tickets;
	}
	
	public Ticket saveOrUpdate(Ticket ticket) {
		if(ticket==null) {
			return null;
		}
		Ticket ticketSaved = this.ticketServiceStub.saveOrUpdate(ticket);
		return ticketSaved;
	}
	
	public EmailDto generateTicket(TicketDto ticketDto) {
		if(ticketDto==null) {
			return null;
		}
		EmailDto emailTicket = this.ticketServiceStub.generateTicket(ticketDto);
		return emailTicket;
	}
	
	public float calculatePrice(Ticket ticket) {
		if(ticket==null) {
			return -1;
		}
		//GET FARE COST PER KM
		IdDto fareId = IdDto.newBuilder()
				.setId(ticket.getFareId())
				.build();
		Fare ticketFare =  this.fareServiceStub.findOne(fareId);
		if(ticketFare==null) {
			return -1;
		}
		float costPerKm = ticketFare.getPrice();
		//GET DISTANCE
		IdDto flightId = IdDto.newBuilder()
				.setId(ticket.getFlightId())
				.build();
		Flight flight = this.flightServiceStub.findOne(flightId);
		if(flight==null) {
			return -1;
		}
		float distance = flight.getDistance();
		float price = distance*costPerKm;
		return price;
	}
	
	public int calculateGeneratedPoints(String flightId) {
		if(flightId==null) {
			return -1;
		}
		IdDto id = IdDto.newBuilder()
				.setId(flightId)
				.build();
		Flight flight = this.flightServiceStub.findOne(id);
		int points = (int) flight.getDistance();
		return points;
	}
	
	public boolean delete(String ticketId) {
		if(ticketId==null) {
			return false;
		}
		TicketDto id = TicketDto.newBuilder()
				.setTicketId(ticketId)
				.build();
		Ticket ticket = this.ticketServiceStub.delete(id);
		if(ticket==null) {
			return false;
		}
		return true;
	}
}
