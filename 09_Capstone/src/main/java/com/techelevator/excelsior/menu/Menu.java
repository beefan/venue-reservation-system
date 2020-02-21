package com.techelevator.excelsior.menu;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Scanner;

import com.techelevator.excelsior.model.Space;
import com.techelevator.excelsior.model.Venue;

public class Menu {

	Scanner scanner = new Scanner(System.in);

	public String displayMainMenu() {
		System.out.println("What would you like to do?");
		System.out.println("1) List Venues");
		System.out.println("Q) Quit");

		return getUserChoice("[1Q]");
	}

	public String displayVenues(List<Venue> venues) {
		String regex = "[R";
		int counter = 1;

		System.out.println("Which venue would you like to view?");
		for (Venue venue : venues) {
			System.out.println(counter + ") " + venue.getName());
			regex += counter;
			counter++;
		}

		regex += "]";

		System.out.println("R) Return to Previous Screen");

		return getUserChoice(regex);

	}

	public String displayVenueDetails(Venue venue) {
		System.out.println();
		System.out.println(venue.getName());
		System.out.println("Location: " + venue.getCityName() + ", " + venue.getStateName());
		System.out.println();
		System.out.print("Categories: ");
		boolean isFirst = true;
		for (String category : venue.getCategories()) {
			if (!isFirst) {
				System.out.print(", " + category);
			} else {
				System.out.print(category);
				isFirst = false;
			}
		}
		System.out.println();
		System.out.println("\n" + venue.getDescription());
		System.out.println("\nWhat would you like to do next?");
		System.out.println("1) View Spaces");
		System.out.println("R) Return to Previous Screen");

		return getUserChoice("[1R]");
	}

	public String displayVenueSpaces(Venue venue) {

		int indexVariable = 1;

		System.out.println();
		System.out.println(venue.getName());
		System.out.println();
		System.out.printf("%-2s %-30s %-12s %-12s %-15s %-20s\n", " ", "Name", "Open", "Close", "Daily Rate",
				"Max. Occupancy");
		for (Space space : venue.getSpaces()) {
			System.out.printf("%s %-30s %-12s %-12s $%-19.2f %-20d\n", "#" + indexVariable, space.getName(),
					getMonth(space.getOpenFrom()), getMonth(space.getOpenTo()), space.getDailyRate(),
					space.getMaxOccupancy());
			indexVariable++;
		}
		System.out.println();
		System.out.println("What would you like to do next?");
		System.out.println("1) Reserve a Space");
		System.out.println("R) Return to Previous Screen");

		return getUserChoice("[1R]");

	}

	public String getReservationStartDateFromUser() {
		// TODO Reserve A Space Menu
		return null;
	}

	private String getUserChoice(String regex) {
		String userInput = "";

		while (true) {
			userInput = scanner.nextLine();
			if (userInput.matches(regex)) {
				break;
			} else {
				System.out.println("\nInvalid Option. Try again. \n");
			}
		}
		return userInput;
	}

	private String getMonth(int month) {
		if (month == 0) {
			return "";
		}
		return new DateFormatSymbols().getMonths()[month - 1];
	}

}
