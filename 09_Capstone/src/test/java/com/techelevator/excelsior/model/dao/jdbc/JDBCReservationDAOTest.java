package com.techelevator.excelsior.model.dao.jdbc;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.DAOIntegrationTest;
import com.techelevator.excelsior.model.Reservation;

public class JDBCReservationDAOTest extends DAOIntegrationTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCReservationDAO dao;
	private JdbcTemplate jdbcTemplate;

	@Before
	public void setup() {
		dataSource = (SingleConnectionDataSource) super.getDataSource();
		dao = new JDBCReservationDAO(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void add_reservation() {
		truncateReservation();

		// add reservation
		long spaceId = 3;
		int numberOfAttendees = 50;
		LocalDate startDate = LocalDate.of(2020, 6, 6);
		LocalDate endDate = LocalDate.of(2020, 6, 9);
		String reservedFor = "Martha";

		Reservation reservation = dao.addReservation(spaceId, numberOfAttendees, startDate, endDate, reservedFor);

		String sql = "SELECT reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for "
				+ "FROM reservation WHERE reserved_for = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, reservedFor);

		if (row.next()) {
			Assert.assertEquals(spaceId, row.getLong("space_id"));
			Assert.assertEquals(numberOfAttendees, row.getInt("number_of_attendees"));
			Assert.assertEquals(startDate, row.getDate("start_date").toLocalDate());
			Assert.assertEquals(endDate, row.getDate("end_date").toLocalDate());
			Assert.assertEquals(reservedFor, row.getString("reserved_for"));
		}

		// Test returns reservation object
		Assert.assertNotNull(reservation);
		Assert.assertEquals(row.getLong("reservation_id"), reservation.getId());
		Assert.assertEquals("Marvin Gardens", reservation.getSpace());
		Assert.assertEquals("Hidden Owl Eatery", reservation.getVenue());
		Assert.assertEquals(numberOfAttendees, reservation.getNumberOfAttendees());
		Assert.assertEquals(startDate, reservation.getStartDate());
		Assert.assertEquals(endDate, reservation.getEndDate());
		Assert.assertEquals(reservedFor, reservation.getReservedFor());

	}

	// Test upcoming reservations
	@Test
	public void get_upcoming_reservations() {
		// Setup reservation table
		truncateReservation();

		// space_id 1 is in venue_id 1
		long venueId = 1;
		long spaceId = 1;
		LocalDate startDate = LocalDate.now().plusDays(10);
		LocalDate endDate = LocalDate.now().plusDays(14);

		// Add one reservations for test venue in 30 day range
		String sql = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for) "
				+ "VALUES (DEFAULT, ?, 4, ?, ?, 'John Doe')";
		jdbcTemplate.update(sql, spaceId, startDate, endDate);

		// Add one reservations for different venue in 30 day range
		sql = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for) "
				+ "VALUES (DEFAULT, 8, 4, ?, ?, 'John Doe')";
		jdbcTemplate.update(sql, startDate, endDate);

		startDate = LocalDate.now().minusDays(10);
		endDate = LocalDate.now().plusDays(1);

		// Add reservation before now
		sql = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for) "
				+ "VALUES (DEFAULT, ?, 4, ?, ?, 'John Doe')";
		jdbcTemplate.update(sql, spaceId, startDate, endDate);

		startDate = LocalDate.now().plusDays(40);
		endDate = LocalDate.now().plusDays(45);

		// Add reservation beyond 30 day range
		sql = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for) "
				+ "VALUES (DEFAULT, ?, 4, ?, ?, 'John Doe')";
		jdbcTemplate.update(sql, spaceId, startDate, endDate);

		List<Reservation> reservation = dao.getUpcomingReservations(venueId);

		Assert.assertEquals(1, reservation.size());

	}

	private void truncateReservation() {
		String sql = "TRUNCATE reservation CASCADE";
		jdbcTemplate.update(sql);
	}

}
