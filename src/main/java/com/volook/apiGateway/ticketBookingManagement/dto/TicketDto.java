package com.volook.apiGateway.ticketBookingManagement.dto;

public record TicketDto (
		String id,
		String passengerName,
		String passengerSurname,
		String fareId,
		String customerCode,
		float price,
		int generatedPoints,
		int usedPoints,
		String flightId,
		long flightDate,
		String bookingId,
		String userId,
		String from,
		String to
)
{}
