package com.volook.apiGateway.flightsManagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.volook.apiGateway.flightsManagement.dto.AvailableFlight;
import com.volook.apiGateway.flightsManagement.services.FlightService;

import flightsManager.Flights.AvailableFlights;
import flightsManager.Flights.Flight;
import flightsManager.Flights.PaginatedFlights;
import flightsManager.Flights.SearchFlightsDto;

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
	
	@GetMapping("/generateFlights")
	public ResponseEntity<List<AvailableFlight>> generateFlights(@RequestParam(name = "from")String from,
			@RequestParam(name = "to")String to, @RequestParam(name = "departureDate")long departureDate,
			@RequestParam(name = "fare")String fareId){
		try {
			List<AvailableFlight> flights = this.flightService.generateFlights(from,to,departureDate,fareId);
			if(flights!=null) {
				return ResponseEntity.ok(flights);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<List<AvailableFlight>>(HttpStatus.BAD_REQUEST);
	}
	
}
