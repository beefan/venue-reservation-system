package com.techelevator.excelsior;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.excelsior.menu.Menu;
import com.techelevator.excelsior.model.Reservation;
import com.techelevator.excelsior.model.Venue;

public class ExcelsiorCLI {

	private Menu menu;
	private BookingAgent bookingAgent;
	private boolean isRunning = true;

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/excelsior-venues");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		Menu menu = new Menu();

		ExcelsiorCLI application = new ExcelsiorCLI(dataSource, menu);
		application.run();
	}

	public ExcelsiorCLI(DataSource datasource, Menu menu) {
		bookingAgent = new BookingAgent(datasource);

		this.menu = menu;

	}

	public void run() {
		while (isRunning) {
			switch (menu.displayMainMenu()) {
			case "1":
				viewVenues();
				break;
			case "Q":
				isRunning = false;
				break;
			}
		}

	}

	private void viewVenues() {
		boolean isLocalRunning = true;
		while (isLocalRunning && isRunning) {
			String userChoice = menu.displayVenues(bookingAgent.getVenues());
			if (userChoice.equals("R")) {
				isLocalRunning = false;
				break;
			} else {
				venueDetails(bookingAgent.getVenues().get(Integer.valueOf(userChoice) - 1));
			}
		}

	}

	private void venueDetails(Venue venue) {
		boolean isLocalRunning = true;
		while (isLocalRunning && isRunning) {
			switch (menu.displayVenueDetails(venue)) {
			case "1":
				listVenueSpaces(venue);
				break;
			case "R":
				isLocalRunning = false;
				break;
			}
		}
	}

	private void listVenueSpaces(Venue venue) {
		boolean isLocalRunning = true;
		while (isLocalRunning && isRunning) {
			switch (menu.displayVenueSpaces(venue)) {
			case "1":
				reserveASpace(venue);
				break;
			case "R":
				isLocalRunning = false;
				break;
			}
		}
	}

	private void reserveASpace(Venue venue) {
		boolean isLocalRunning = true;
		while (isLocalRunning && isRunning) {
			LocalDate startDate = menu.getReservationStartDateFromUser();
			LocalDate endDate = menu.getReservationEndDateFromUser(startDate);
			int numberOfAttendees = menu.getNumberOfAttendeesFromUser();
			String spaceIdValidation = menu.displayAvailableSpacesToUser(
					bookingAgent.getAvailableSpacesForVenue(venue.getId(), startDate, endDate, numberOfAttendees),
					ChronoUnit.DAYS.between(startDate, endDate));
			long spaceId = menu.getSpaceIdFromUser(spaceIdValidation);
			if (spaceId == 0) {
				isLocalRunning = false;
				break;
			}
			String reservedFor = menu.getReservedForFromUser();
			Reservation reservation = bookingAgent.addReservation(spaceId, numberOfAttendees, startDate, endDate,
					reservedFor);
			reservationConfirmation(reservation);
		}
	}

	private void reservationConfirmation(Reservation reservation) {
		menu.displayReservationDetailsToUser(reservation);
		exitStrategy();
	}

	private void exitStrategy() {
		while (isRunning) {
			switch (menu.displayExitMenu()) {
			case "R":
				run();
				break;
			case "Q":
				isRunning = false;
				break;
			}
		}
	}

}
