package com.volook.apiGateway.ticketBookingManagement.services;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import net.devh.boot.grpc.client.inject.GrpcClient;
import ticketBookingManager.TicketBooking.EmailDto;
import ticketBookingManager.TicketBooking.PaginateQuery;
import ticketBookingManager.TicketBooking.PaginatedFidelityStatistics;
import ticketBookingManager.TicketBooking.PaginatedTickets;
import ticketBookingManager.TicketBooking.Ticket;
import ticketBookingManager.TicketBooking.TicketDto;
import ticketBookingManager.TicketBooking.TicketId;
import ticketBookingManager.TicketBooking.UserId;
import ticketBookingManager.TicketServiceGrpc.TicketServiceBlockingStub;

@Service
public class TicketService {
	
	@GrpcClient(Microservice.TICKET_BOOKING_MANAGER)
	private TicketServiceBlockingStub ticketServiceStub;
	
	public Ticket findOne(String ticketId, String userId) {
		if(ticketId==null) {
			return null;
		}
		TicketDto ticketDto = TicketDto.newBuilder()
				.setUserId(userId)
				.setTicketId(ticketId)
				.build();
		Ticket ticket = this.ticketServiceStub.findOne(ticketDto);
		return ticket;
	}
	
	public PaginatedTickets find(String query) {
		if(query==null) {
			return null;
		}
		PaginateQuery paginateQuery = PaginateQuery.newBuilder()
				.setQuery(query)
				.build();
		PaginatedTickets tickets = this.ticketServiceStub.find(paginateQuery);
		return tickets;
	}
	
	public Ticket saveOrUpdate(Ticket ticket) {
		if(ticket==null) {
			return null;
		}
		Ticket ticketSaved = this.ticketServiceStub.saveOrUpdate(ticket);
		return ticketSaved;
	}
	
	public EmailDto generateTicket(String ticketId) {
		if(ticketId==null) {
			return null;
		}
		TicketId id = TicketId.newBuilder()
				.setId(ticketId)
				.build();
		EmailDto emailTicket = this.ticketServiceStub.generateTicket(id);
		return emailTicket;
	}
	
	public PaginatedFidelityStatistics getFidelityStatistics(String userId) {
		if(userId==null) {
			return null;
		}
		UserId id = UserId.newBuilder()
				.setId(userId)
				.build();
		PaginatedFidelityStatistics statistics = this.ticketServiceStub.getStatistics(id);
		return statistics;
	}
}
