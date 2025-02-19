package com.volook.apiGateway.flightsManagement.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import com.volook.apiGateway.flightsManagement.dto.AirportDto;
import com.volook.apiGateway.flightsManagement.dto.AvailableFlight;
import com.volook.apiGateway.flightsManagement.dto.FareDto;
import com.volook.apiGateway.flightsManagement.dto.PromotionDto;

import flightsManager.Flights.Airport;
import flightsManager.Flights.Fare;
import flightsManager.Flights.Flight;
import flightsManager.Flights.Promotion;
import ticketBookingManager.TicketBooking.CountDto;
import ticketBookingManager.TicketServiceGrpc.TicketServiceBlockingStub;

public class GenerateFlight implements Callable<List<AvailableFlight>> {

	private Flight flight;
	private long selectedDate;
	private TicketServiceBlockingStub ticketServiceBlockingStub;
	private final long dayAfterSelectedDate;
	
	private final long HOURLY_FACTOR = (60 * 60 * 1000);
	private final long DAILY_FACTOR = HOURLY_FACTOR * 24;
	private final long WEEKLY_FACTOR = DAILY_FACTOR * 7;
	private final long MONTHLY_FACTOR = DAILY_FACTOR * 30;
	
	private final long[] timeFactor = {HOURLY_FACTOR, DAILY_FACTOR, WEEKLY_FACTOR,MONTHLY_FACTOR};
	
	public GenerateFlight(Flight flight, long selectedDate, TicketServiceBlockingStub ticketServiceBlockingStub) {
		if (flight == null) {
			throw new IllegalArgumentException();
		}
		this.flight = flight;
		this.selectedDate = setToMidnight(selectedDate);
		this.ticketServiceBlockingStub = ticketServiceBlockingStub;
		this.dayAfterSelectedDate = selectedDate + DAILY_FACTOR;
	}

	@Override
	public LinkedList<AvailableFlight> call() throws Exception {
		LinkedList<AvailableFlight> availableFlights = new LinkedList<>();
		long curDate = flight.getStartDateTime();//setToMidnight(selectedDate);
		
		while (curDate <= flight.getEndDateTime() && curDate<dayAfterSelectedDate) {
			long offset = timeFactor[flight.getFrequencyType().ordinal()]*flight.getFrequency();
			long nextDepartureDate = curDate + offset;
			nextDepartureDate = (new Date(nextDepartureDate)).getTime();
			if(selectedDate<dayAfterSelectedDate && selectedDate<=curDate) {
				CountDto countDto = CountDto.newBuilder()
						.setDepartureDate(nextDepartureDate)
						.setFlightId(flight.getId())
						.build();
				int occupiedSeats = this.ticketServiceBlockingStub.countTickets(countDto).getNumberOfTickets();
				if(occupiedSeats<flight.getSeats()) {
					/*Airport departure = Airport.newBuilder()
							.setId(flight.getDeparture().getId().toString())
							.setName(flight.getDeparture().getName())
							.setMunicipality(flight.getDeparture().getMunicipality())
							.setMunicipalityCode(flight.getDeparture().getMunicipalityCode())
							.setNationalCode(flight.getDeparture().getNationalCode())
							.build();*/
					AirportDto departure = new AirportDto(
							flight.getDeparture().getId().toString(),
							flight.getDeparture().getName(),
							flight.getDeparture().getIata(),
							flight.getDeparture().getLatitude(),
							flight.getDeparture().getLongitude()
							);
					
					
					/*Airport destination = Airport.newBuilder()
							.setId(flight.getDestination().getId().toString())
							.setName(flight.getDestination().getName())
							.setMunicipality(flight.getDestination().getMunicipality())
							.setMunicipalityCode(flight.getDestination().getMunicipalityCode())
							.setNationalCode(flight.getDestination().getNationalCode())
							.build();*/
					AirportDto destination = new AirportDto(
							flight.getDestination().getId().toString(),
							flight.getDestination().getName(),
							flight.getDestination().getIata(),
							flight.getDestination().getLatitude(),
							flight.getDestination().getLongitude()
							);
					
					/*Promotion promotion = Promotion.newBuilder()
							.setId(flight.getPromotion().getId().toString())
							.setName(flight.getPromotion().getName())
							.setDiscountPercentage(flight.getPromotion().getDiscountPercentage())
							.setEndDate(flight.getPromotion().getEndDate())
							.setStartDate(flight.getPromotion().getStartDate())
							.setOnlyForLoyalCustomer(flight.getPromotion().getOnlyForLoyalCustomer())
							.build();*/
					
					PromotionDto promotion = new PromotionDto (
							flight.getPromotion().getId().toString(),
							flight.getPromotion().getName(),
							flight.getPromotion().getDiscountPercentage(),
							flight.getPromotion().getEndDate(),
							flight.getPromotion().getStartDate(),
							flight.getPromotion().getOnlyForLoyalCustomer()
					);
					
					LinkedList<FareDto> fares = new LinkedList<>();
					for(Fare fare: flight.getFaresList()) {
						FareDto curFare = new FareDto (
								fare.getEditable(),
								fare.getId().toString(),
								fare.getModificationPrice(),
								fare.getName(),
								fare.getPrice()
						);
						/*Fare curFare = Fare.newBuilder()
								.setEditable(fare.getEditable())
								.setId(fare.getId().toString())
								.setModificationPrice(fare.getModificationPrice())
								.setName(fare.getName())
								.setPrice(fare.getPrice())
								.build();*/
						fares.add(curFare);
					}
					AvailableFlight availableFlight = new AvailableFlight(
							flight.getId().toString(),
							flight.getName(),
							nextDepartureDate,
							flight.getDistance(),
							departure,
							destination,
							promotion,
							fares.isEmpty()?null:fares);
					availableFlights.add(availableFlight);
					curDate = nextDepartureDate;
				}
			}else {
				curDate = nextDepartureDate;
			}
		}
		return availableFlights;
	}
	
	private long setToMidnight(long milliseconds) {
        Date date = new Date(milliseconds);
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
	}

}
