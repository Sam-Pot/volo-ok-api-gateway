package com.volook.apiGateway.flightsManagement.dto;

public record PromotionDto (
		String id,
		String name,
		float discountPercentage,
		long endDate,
		long startDate,
		boolean onlyForLoyalCustomer
)
{}