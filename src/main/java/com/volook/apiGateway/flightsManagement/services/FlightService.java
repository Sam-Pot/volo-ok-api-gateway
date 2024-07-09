package com.volook.apiGateway.flightsManagement.services;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import flightsManager.FlightServiceGrpc.FlightServiceBlockingStub;
import flightsManager.Flights.Flight;
import flightsManager.Flights.FlightId;
import flightsManager.Flights.PaginateQuery;
import flightsManager.Flights.PaginatedFlights;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class FlightService {

	@GrpcClient(Microservice.TICKET_BOOKING_MANAGER)
	private FlightServiceBlockingStub flightServiceStub;
	
	public Flight findOne(String flightId) {
		if(flightId==null) {
			return null;
		}
		FlightId id = FlightId.newBuilder()
				.setId(flightId)
				.build();
		Flight flight = this.flightServiceStub.findOne(id);
		return flight;
	}
	
	public PaginatedFlights find(String query) {
		if(query==null) {
			return null;
		}
		PaginateQuery paginateQuery = PaginateQuery.newBuilder()
				.setQuery(query)
				.build();
		PaginatedFlights flights = this.flightServiceStub.find(paginateQuery);
		return flights;
	}
	
	public Flight saveOrUpdate(Flight flight) {
		if(flight==null) {
			return null;
		}
		Flight savedFlight = this.flightServiceStub.saveOrUpdate(flight);
		return savedFlight;
	}
	
	public Flight delete(String flightId) {
		if(flightId==null) {
			return null;
		}
		FlightId id = FlightId.newBuilder()
				.setId(flightId)
				.build();		
		return this.flightServiceStub.delete(id);
	}
}
