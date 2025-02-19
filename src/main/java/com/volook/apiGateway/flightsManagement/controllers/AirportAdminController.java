package com.volook.apiGateway.flightsManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volook.apiGateway.flightsManagement.services.AirportService;

import flightsManager.Flights.Airport;
import flightsManager.Flights.IdDto;
import flightsManager.Flights.PaginatedAirports;
import ticketBookingManager.TicketBooking.Booking;

@RestController
@RequestMapping("/airports")
public class AirportAdminController {

	@Autowired
	private AirportService airportService;
	
	@GetMapping()
	public ResponseEntity<PaginatedAirports> findAll(){
		PaginatedAirports airports = this.airportService.findAll();
		if(airports!=null) {
			return ResponseEntity.ok(airports);
		}
		return new ResponseEntity<PaginatedAirports>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Airport> findOne(@PathVariable("id") String id){
		Airport airport = this.airportService.findOne(id);
		if(airport!=null) {
			return ResponseEntity.ok(airport);
		}
		return new ResponseEntity<Airport>(HttpStatus.BAD_REQUEST);
	}
	
}
