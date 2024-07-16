package com.volook.apiGateway.ticketBookingManagement.dto;

public record BillingInformationsDto(
		String cardHolderName,
		String cardHolderSurname,
		int expiryMonth,
		int expiryYear,
		int cvv,
		String PAN,
		float cost
		)
{}
