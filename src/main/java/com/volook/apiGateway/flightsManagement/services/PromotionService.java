package com.volook.apiGateway.flightsManagement.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.volook.apiGateway.Microservice;

import flightsManager.Flights.IdDto;
import flightsManager.Flights.PaginatedPromotions;
import flightsManager.Flights.Promotion;
import flightsManager.Flights.QueryDto;
import flightsManager.PromotionServiceGrpc.PromotionServiceBlockingStub;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class PromotionService {
	
	@GrpcClient(Microservice.FLIGHTS_MANAGER)
	private PromotionServiceBlockingStub promotionServiceStub;
	
	public Promotion saveOrUpdate(Promotion promotion) {
		if(promotion==null) {
			return null;
		}
		Promotion savedPromotion = this.promotionServiceStub.saveOrUpdate(promotion);
		return savedPromotion;
	}
	
	public Promotion delete(String promotionId) {
		if(promotionId==null) {
			return null;
		}
		IdDto id = IdDto.newBuilder()
				.setId(promotionId)
				.build();
		Promotion deletedPromotion = this.promotionServiceStub.delete(id);
		return deletedPromotion;
	}
	
	public Promotion findOne(String promotionId) {
		if(promotionId==null) {
			return null;
		}
		IdDto id = IdDto.newBuilder()
				.setId(promotionId)
				.build();
		Promotion promotion = this.promotionServiceStub.findOne(id);
		return promotion;
	}
	
	public PaginatedPromotions find() {
		QueryDto paginateQuery = QueryDto.newBuilder()
				.setQuery("a")
				.build();
		PaginatedPromotions promotions = this.promotionServiceStub.findAll(paginateQuery);
		return promotions;
	}
	
	public List<Promotion> getLoyaltyCustomerPromotions(){
		List<Promotion> promotions = new ArrayList<>();
		QueryDto mockQuery = QueryDto.newBuilder().build();
		PaginatedPromotions response = this.promotionServiceStub.getLoyaltyCustomerPromotions(mockQuery);
		for(Promotion p: response.getPromotionsList()) {
			promotions.add(p);
		}
		return null;
	}
	
}
