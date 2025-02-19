package com.volook.apiGateway.flightsManagement.services;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import flightsManager.AirportServiceGrpc.AirportServiceBlockingStub;
import flightsManager.Flights.Airport;
import flightsManager.Flights.IdDto;
import flightsManager.Flights.PaginatedAirports;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class AirportService{
	
	@GrpcClient(Microservice.FLIGHTS_MANAGER)
	private AirportServiceBlockingStub airportServiceStub;
	
	public PaginatedAirports findAll() {
		IdDto idDto = IdDto.newBuilder()
				.setId("a")
				.build();
		PaginatedAirports airports = this.airportServiceStub.findAll(idDto);
		return airports;
	}
	
	public Airport findOne(String id) {
		IdDto idDto = IdDto.newBuilder().setId(id).build();
		Airport airport = this.airportServiceStub.findOne(idDto);
		return airport;
	}
}
