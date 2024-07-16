package com.volook.apiGateway.flightsManagement.services;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import flightsManager.FareServiceGrpc.FareServiceBlockingStub;
import flightsManager.Flights.Fare;
import flightsManager.Flights.IdDto;
import flightsManager.Flights.PaginatedFares;
import flightsManager.Flights.QueryDto;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class FareService {
	
	@GrpcClient(Microservice.FLIGHTS_MANAGER)
	private FareServiceBlockingStub fareServiceStub;
	
	public Fare saveOrUpdate(Fare fare) {
		if(fare==null) {
			return null;
		}
		Fare savedFare = this.fareServiceStub.saveOrUpdate(fare);
		return savedFare;
	}
	
	public Fare delete(String fareId) {
		if(fareId==null) {
			return null;
		}
		IdDto id = IdDto.newBuilder()
				.setId(fareId)
				.build();
		Fare deletedFare = this.fareServiceStub.delete(id);
		return deletedFare;
	}
	
	public PaginatedFares find() {
		QueryDto paginateQuery = QueryDto.newBuilder()
				.setQuery("a")
				.build();
		PaginatedFares fares = this.fareServiceStub.findAll(paginateQuery);
		return fares;
	}
	
	public Fare findOne(String fareId) {
		if(fareId==null) {
			return null;
		}
		IdDto id = IdDto.newBuilder()
				.setId(fareId)
				.build();
		Fare fare = this.fareServiceStub.findOne(id);
		return fare;
	}
}
