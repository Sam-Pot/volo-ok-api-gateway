package com.volook.apiGateway.flightsManagement.dto;

public record AirportDto (
		String id,
		String name,
		String municipality,
		String municipalityCode,
		String nationalCode
){}
