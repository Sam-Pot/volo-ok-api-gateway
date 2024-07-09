package com.volook.apiGateway.ticketBookingManagement.dto;

import paymentManager.Payment.BillingInformations;
import ticketBookingManager.TicketBooking.Ticket;

public record BuyTicketDto(
		Ticket ticket,
		BillingInformations billingInformation
) {}
