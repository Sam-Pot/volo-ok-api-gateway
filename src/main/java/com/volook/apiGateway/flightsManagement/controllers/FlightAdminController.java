package com.volook.apiGateway.flightsManagement.controllers;

import java.util.Map;

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

import com.volook.apiGateway.flightsManagement.services.AirportService;
import com.volook.apiGateway.flightsManagement.services.FareService;
import com.volook.apiGateway.flightsManagement.services.FlightService;
import com.volook.apiGateway.flightsManagement.services.PromotionService;

import flightsManager.Flights.Airport;
import flightsManager.Flights.Flight;
import flightsManager.Flights.FlightDto;
import flightsManager.Flights.PaginatedFlights;


@RestController
@RequestMapping("/admin/flights")
public class FlightAdminController {
	
	@Autowired
	private FlightService flightService;
	@Autowired
	private AirportService airportService;
	@Autowired
	private PromotionService promotionService;
	@Autowired
	private FareService fareService;

	
	@GetMapping("/{id}")
	public ResponseEntity<Flight> findOne(@PathVariable("id") String flightId){		
		Flight flight = this.flightService.findOne(flightId);
		if(flight!=null) {
			return ResponseEntity.ok(flight);
		}
		return new ResponseEntity<Flight>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedFlights> find(@RequestParam Map<String,String> mapQuery){
		String query = "";
		for(String s: mapQuery.keySet()) {
			query+=s+"="+mapQuery.get(s);
		}
		PaginatedFlights flights = this.flightService.find(query);
		if(flights!=null) {
			return ResponseEntity.ok(flights);
		}
		return new ResponseEntity<PaginatedFlights>(HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping()
	public ResponseEntity<Flight> update(@RequestBody FlightDto flight) {
		Flight updatedFlights = this.flightService.saveOrUpdate(flight);
		if(updatedFlights!=null) {
			return ResponseEntity.ok(updatedFlights);
		}
		return new ResponseEntity<Flight>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping()
	public ResponseEntity<Flight> save(@RequestBody FlightDto flight) {
		Flight savedFlights = this.flightService.saveOrUpdate(flight);
		if(savedFlights!=null) {
			return ResponseEntity.ok(savedFlights);
		}
		return new ResponseEntity<Flight>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Flight> delete(@PathVariable("id") String flightId){
		Flight flightDeleted = this.flightService.delete(flightId);
		if(flightDeleted!=null) {
			return ResponseEntity.ok(flightDeleted);
		}
		return new ResponseEntity<Flight>(HttpStatus.BAD_REQUEST);
	}
}
