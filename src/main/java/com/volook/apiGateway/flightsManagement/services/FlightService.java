package com.volook.apiGateway.flightsManagement.services;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;
import com.volook.apiGateway.flightsManagement.dto.AvailableFlight;
import com.volook.apiGateway.flightsManagement.utils.GenerateFlight;

import flightsManager.FareServiceGrpc.FareServiceBlockingStub;
import flightsManager.FlightServiceGrpc.FlightServiceBlockingStub;
import flightsManager.Flights.AvailableFlights;
import flightsManager.Flights.Fare;
import flightsManager.Flights.Flight;
import flightsManager.Flights.FlightDto;
import flightsManager.Flights.IdDto;
import flightsManager.Flights.PaginatedFlights;
import flightsManager.Flights.QueryDto;
import flightsManager.Flights.SearchFlightsDto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import ticketBookingManager.TicketServiceGrpc.TicketServiceBlockingStub;

@Service
public class FlightService {

	@GrpcClient(Microservice.FLIGHTS_MANAGER)
	private FlightServiceBlockingStub flightServiceStub;
	
	@GrpcClient(Microservice.TICKET_BOOKING_MANAGER)
	private TicketServiceBlockingStub ticketServiceBlockingStub;
	
	@GrpcClient(Microservice.FLIGHTS_MANAGER)
	private FareServiceBlockingStub fareServiceBlockingStub;
	
	public Flight findOne(String flightId) {
		if(flightId==null) {
			return null;
		}
		IdDto id = IdDto.newBuilder()
				.setId(flightId)
				.build();
		Flight flight = this.flightServiceStub.findOne(id);
		return flight;
	}
	
	public PaginatedFlights find(String query) {
		if(query==null) {
			return null;
		}
		QueryDto paginateQuery = QueryDto.newBuilder()
				.setQuery(query)
				.build();
		PaginatedFlights flights = this.flightServiceStub.find(paginateQuery);
		return flights;
	}
	
	public Flight saveOrUpdate(FlightDto flight) {
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
		IdDto id = IdDto.newBuilder()
				.setId(flightId)
				.build();		
		return this.flightServiceStub.delete(id);
	}
	
	public List<AvailableFlight> generateFlights(String from, String to, long departureDate, String fareId) throws Exception{
		if(from==null || to==null || departureDate<=0 || fareId==null) {
			return null;
		}
		Fare fare = this.fareServiceBlockingStub.findOne(IdDto.newBuilder().setId(fareId).build());
		SearchFlightsDto searchFlightsDto = SearchFlightsDto.newBuilder()
				.setDepartureAirportId(from)
				.setDestinationAirportId(to)
				.setDepartureDate(departureDate)
				.setFare(fare)
				.build();
		AvailableFlights availableFlight = this.flightServiceStub.generateFlights(searchFlightsDto);
		List<AvailableFlight> availableFlights = new LinkedList<>();
		ExecutorService executor = Executors.newCachedThreadPool();
		for (Flight curFlight : availableFlight.getAvailableFlightList()) {
			GenerateFlight generator = new GenerateFlight(curFlight,searchFlightsDto.getDepartureDate(),this.ticketServiceBlockingStub);
			Future<List<AvailableFlight>> generatedFlight = executor.submit(generator);
			availableFlights.addAll(generatedFlight.get());
		}
		executor.shutdown();
		if(availableFlight.getAvailableFlightList().size()==0) {
			executor.shutdownNow();
		}else {
			executor.awaitTermination(30, TimeUnit.SECONDS);
		}
		return availableFlights;
	}
	
}
