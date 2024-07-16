package com.volook.apiGateway.flightsManagement.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import flightsManager.Flights.Airport;
import flightsManager.Flights.Fare;
import flightsManager.Flights.Promotion;

public record AvailableFlight(
		String id, 
		String name, 
		long departureDateTime, 
		float distance, 
		AirportDto departure,
		AirportDto destination, 
		PromotionDto promotion, 
		List<FareDto> fares
) 
{}
