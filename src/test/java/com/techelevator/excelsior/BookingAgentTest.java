package com.techelevator.excelsior;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.DAOIntegrationTest;
import com.techelevator.excelsior.model.Reservation;
import com.techelevator.excelsior.model.Space;
import com.techelevator.excelsior.model.Venue;

public class BookingAgentTest extends DAOIntegrationTest {
	private static SingleConnectionDataSource dataSource;
	private BookingAgent bookingAgent;

	@Before
	public void setup() {
		dataSource = (SingleConnectionDataSource) super.getDataSource();
		bookingAgent = new BookingAgent(dataSource);
	}

	// Test for getVenues
	@Test
	public void get_venues_test() {
		List<Venue> venues = bookingAgent.getVenues();

		Assert.assertNotNull(venues);
		Assert.assertTrue(venues.size() > 0);

		// Test that it populates a venue object
		Assert.assertNotNull(venues.get(0).getId());
		Assert.assertNotNull(venues.get(0).getCityName());
		Assert.assertNotNull(venues.get(0).getDescription());
		Assert.assertNotNull(venues.get(0).getName());
		Assert.assertNotNull(venues.get(0).getStateName());
		Assert.assertNotNull(venues.get(0).getCategories());
		Assert.assertNotNull(venues.get(0).getSpaces());

		// Test for alphabetical order
		Assert.assertTrue(venues.get(0).getName().compareTo(venues.get(1).getName()) < 0);
	}

	// Test for getAvailableSpacesForVenue
	@Test
	public void get_available_spaces_for_venue() {
		LocalDate startDate = LocalDate.of(2020, 02, 18);
		LocalDate endDate = LocalDate.of(2020, 02, 22);

		List<Space> spaces = bookingAgent.getAvailableSpacesForVenue(1, startDate, endDate, 50);

		Assert.assertNotNull(spaces);

		// Test that it populates and that it doesn't list more than 5 spaces
		Assert.assertTrue(spaces.size() > 0 && spaces.size() <= 5);

		// Test that it populates a venue object
		Assert.assertNotNull(spaces.get(0).getId());
		Assert.assertNotNull(spaces.get(0).getVenueId());
		Assert.assertNotNull(spaces.get(0).getName());
		Assert.assertNotNull(spaces.get(0).getOpenFrom());
		Assert.assertNotNull(spaces.get(0).getDailyRate());
		Assert.assertNotNull(spaces.get(0).getOpenTo());
		Assert.assertNotNull(spaces.get(0).getMaxOccupancy());

	}

	// Test for addReservation
	@Test
	public void add_reservation() {
		// setup reservation
		long spaceId = 5;
		int numberOfAttendees = 50;
		LocalDate startDate = LocalDate.of(2020, 02, 18);
		LocalDate endDate = LocalDate.of(2020, 02, 22);
		String reservedFor = "Mr. Goldbloom";

		Reservation reservation = bookingAgent.addReservation(spaceId, numberOfAttendees, startDate, endDate,
				reservedFor);

		// test for returned reservation object
		Assert.assertNotNull(reservation);

		// Test that it populates a reservation object
		Assert.assertNotNull(reservation.getId());
		Assert.assertNotNull(reservation.getReservedFor());
		Assert.assertNotNull(reservation.getSpace());
		Assert.assertNotNull(reservation.getVenue());
		Assert.assertNotNull(reservation.getEndDate());
		Assert.assertNotNull(reservation.getNumberOfAttendees());
		Assert.assertNotNull(reservation.getStartDate());
		Assert.assertNotNull(reservation.getTotalCost());
	}

}
