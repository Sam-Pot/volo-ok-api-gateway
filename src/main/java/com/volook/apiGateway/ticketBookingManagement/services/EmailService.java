package com.volook.apiGateway.ticketBookingManagement.services;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import emailManager.Email.EmailDto;
import emailManager.Email.EmailResponse;
import emailManager.EmailServiceGrpc.EmailServiceBlockingStub;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class EmailService {

	@GrpcClient(Microservice.EMAIL_MANAGER)
	private EmailServiceBlockingStub emailService;
	
	public boolean sendEmail(EmailDto emailDto) {
		if(emailDto==null) {
			return false;
		}
		EmailResponse sent = this.emailService.sendEmail(emailDto);
		return sent.getSent();
	}
}
