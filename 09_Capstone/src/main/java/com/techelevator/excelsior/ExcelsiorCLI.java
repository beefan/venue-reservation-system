package com.techelevator.excelsior;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.excelsior.menu.Menu;
import com.techelevator.excelsior.model.Venue;

public class ExcelsiorCLI {

	Menu menu;
	BookingAgent bookingAgent;

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
		boolean isRunning = true;
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
		boolean isRunning = true;
		while (isRunning) {
			String userChoice = menu.displayVenues(bookingAgent.getVenues());
			if (userChoice.equals("R")) {
				isRunning = false;
			} else {
				venueDetails(bookingAgent.getVenues().get(Integer.valueOf(userChoice) - 1));
			}
		}

	}

	private void venueDetails(Venue venue) {
		boolean isRunning = true;
		while (isRunning) {
			switch (menu.displayVenueDetails(venue)) {
			case "1":
				// TODO Implement View Spaces
				break;
			case "2":
				// TODO Implement Search for Reservation
				break;
			case "R":
				isRunning = false;
				break;
			}
		}
	}
}
