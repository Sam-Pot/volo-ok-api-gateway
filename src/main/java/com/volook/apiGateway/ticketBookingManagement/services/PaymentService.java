package com.volook.apiGateway.ticketBookingManagement.services;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import net.devh.boot.grpc.client.inject.GrpcClient;
import paymentManager.Payment.BillingInformations;
import paymentManager.Payment.PaymentResponse;
import paymentManager.PaymentServiceGrpc.PaymentServiceBlockingStub;

@Service
public class PaymentService {
	@GrpcClient(Microservice.PAYMENT_MANAGER)
	private PaymentServiceBlockingStub paymentService;
	
	public boolean pay(BillingInformations billingInformations) {
		if(billingInformations==null) {
			return false;
		}
		PaymentResponse response = this.paymentService.pay(billingInformations);
		return response.getResponse();
	}
	
}
