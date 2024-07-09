package com.volook.apiGateway.flightsManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.volook.apiGateway.flightsManagement.services.PromotionService;

import flightsManager.Flights.PaginatedPromotions;
import flightsManager.Flights.Promotion;

@RestController
@RequestMapping("/admin/promotions")
public class PromotionAdminController {
	
	@Autowired
	private PromotionService promotionService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Promotion> findOne(@PathVariable("id") String promotionId){		
		Promotion promotion = this.promotionService.findOne(promotionId);
		if(promotion!=null) {
			return ResponseEntity.ok(promotion);
		}
		return new ResponseEntity<Promotion>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedPromotions> find(@RequestParam String query){
		PaginatedPromotions promotions = this.promotionService.find(query);
		if(promotions!=null) {
			return ResponseEntity.ok(promotions);
		}
		return new ResponseEntity<PaginatedPromotions>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping()
	public ResponseEntity<Promotion> update(@RequestBody Promotion promotion) {
		Promotion updatedPromotion = this.promotionService.saveOrUpdate(promotion);
		if(updatedPromotion!=null) {
			return ResponseEntity.ok(updatedPromotion);
		}
		return new ResponseEntity<Promotion>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping()
	public ResponseEntity<Promotion> save(@RequestBody Promotion promotion) {
		Promotion savedPromotion = this.promotionService.saveOrUpdate(promotion);
		if(savedPromotion!=null) {
			return ResponseEntity.ok(savedPromotion);
		}
		return new ResponseEntity<Promotion>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Promotion> delete(@PathVariable("id") String promotionId){
		Promotion promotion = this.promotionService.delete(promotionId);
		if(promotion!=null) {
			return ResponseEntity.ok(promotion);
		}
		return new ResponseEntity<Promotion>(HttpStatus.BAD_REQUEST);
	}
}
