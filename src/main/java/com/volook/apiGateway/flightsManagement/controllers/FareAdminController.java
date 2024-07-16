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

import com.volook.apiGateway.flightsManagement.services.FareService;
import flightsManager.Flights.Fare;
import flightsManager.Flights.PaginatedFares;

@RestController
@RequestMapping("fares")
public class FareAdminController {
	
	@Autowired
	private FareService fareService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Fare> findOne(@PathVariable("id") String fareId){		
		Fare fare = this.fareService.findOne(fareId);
		if(fare!=null) {
			return ResponseEntity.ok(fare);
		}
		return new ResponseEntity<Fare>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedFares> find(){
		PaginatedFares fares = this.fareService.find();
		if(fares!=null) {
			return ResponseEntity.ok(fares);
		}
		return new ResponseEntity<PaginatedFares>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping()
	public ResponseEntity<Fare> update(@RequestBody Fare fare) {
		Fare updatedFare = this.fareService.saveOrUpdate(fare);
		if(updatedFare!=null) {
			return ResponseEntity.ok(updatedFare);
		}
		return new ResponseEntity<Fare>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping()
	public ResponseEntity<Fare> save(@RequestBody Fare fare) {
		Fare savedFare = this.fareService.saveOrUpdate(fare);
		if(savedFare!=null) {
			return ResponseEntity.ok(savedFare);
		}
		return new ResponseEntity<Fare>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Fare> delete(@PathVariable("id") String fareId){
		Fare fare = this.fareService.delete(fareId);
		if(fare!=null) {
			return ResponseEntity.ok(fare);
		}
		return new ResponseEntity<Fare>(HttpStatus.BAD_REQUEST);
	}
	
}
