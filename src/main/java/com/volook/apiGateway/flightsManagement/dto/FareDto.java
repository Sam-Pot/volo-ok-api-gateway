package com.volook.apiGateway.flightsManagement.dto;

public record FareDto (
		boolean editable,
		String id,
		float modificationPrice,
		String name,
		float price
)
{}