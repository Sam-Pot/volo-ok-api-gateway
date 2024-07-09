package com.volook.apiGateway.flightsManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volook.apiGateway.flightsManagement.services.FlightService;

import flightsManager.Flights.Flight;


@RestController
@RequestMapping("/admin/flights")
public class FlightAdminController {
	
	@Autowired
	private FlightService flightService;
	
	@PutMapping()
	public ResponseEntity<Flight> update(@RequestBody Flight flight) {
		Flight updatedFlights = this.flightService.saveOrUpdate(flight);
		if(updatedFlights!=null) {
			return ResponseEntity.ok(updatedFlights);
		}
		return new ResponseEntity<Flight>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping()
	public ResponseEntity<Flight> save(@RequestBody Flight flight) {
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
