package com.volook.apiGateway.flightsManagement.dto;

public record AirportDto (
		String id,
		String name,
		String iata,
		double latitude,
		double longitude
){}
