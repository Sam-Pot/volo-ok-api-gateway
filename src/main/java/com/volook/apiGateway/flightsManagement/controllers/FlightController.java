package com.volook.apiGateway.flightsManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.volook.apiGateway.flightsManagement.services.FlightService;

import flightsManager.Flights.Flight;
import flightsManager.Flights.PaginatedFlights;

@RestController
@RequestMapping("/flights")
public class FlightController {
	
	@Autowired
	private FlightService flightService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Flight> findOne(@PathVariable("id") String flightId){		
		Flight flight = this.flightService.findOne(flightId);
		if(flight!=null) {
			return ResponseEntity.ok(flight);
		}
		return new ResponseEntity<Flight>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping()
	public ResponseEntity<PaginatedFlights> find(@RequestParam String query){
		PaginatedFlights flights = this.flightService.find(query);
		if(flights!=null) {
			return ResponseEntity.ok(flights);
		}
		return new ResponseEntity<PaginatedFlights>(HttpStatus.BAD_REQUEST);
	}
	
}
