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
import com.techelevator.excelsior.model.Space;

public class JDBCSpaceDAOTest extends DAOIntegrationTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCSpaceDAO dao;
	private JdbcTemplate jdbcTemplate;

	@Before
	public void setup() {
		dataSource = (SingleConnectionDataSource) super.getDataSource();
		dao = new JDBCSpaceDAO(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void get_spaces_by_venue_id() {
		truncateSpace();

		// add a spaceless venue
		String sql = "INSERT INTO venue (id, name, city_id, description) VALUES "
				+ "(DEFAULT, 'The Singing Belle', 3, 'Super sound spot') RETURNING id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long id = row.getLong("id");

		// test empty table returns 0 size
		List<Space> spaces = dao.getSpacesByVenueId(id);
		Assert.assertEquals(0, spaces.size());

		// add one space
		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Mars Rover', " + "true, 2, 10, 1004.00, 2)";
		jdbcTemplate.update(sql, id);

		spaces = dao.getSpacesByVenueId(id);

		// Test for added space
		Assert.assertEquals("Mars Rover", spaces.get(0).getName());
		Assert.assertEquals(1, spaces.size());

		// add another space
		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Red Rover', " + "false, 1, 12, 4.00, 44)";
		jdbcTemplate.update(sql, id);

		spaces = dao.getSpacesByVenueId(id);

		// Test for added space
		Assert.assertEquals("Red Rover", spaces.get(1).getName());
		Assert.assertEquals(2, spaces.size());

		// add a space to a non tested venue
		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, 7, 'Black Rover', " + "false, 1, 12, 4.00, 44)";
		jdbcTemplate.update(sql);

		spaces = dao.getSpacesByVenueId(id);

		// Test for space not added
		Assert.assertEquals(2, spaces.size());

	}

	@Test
	public void get_available_spaces_for_venue() {
		// Setup Reusable variables
		LocalDate startDate;
		LocalDate endDate;
		int numberOfAttendees;
		List<Space> spaces;

		truncateSpace();

		// add a spaceless venue
		String sql = "INSERT INTO venue (id, name, city_id, description) VALUES "
				+ "(DEFAULT, 'The Singing Belle', 3, 'Super sound spot') RETURNING id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long id = row.getLong("id");

		// add one space
		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Mars Rover', "
				+ "true, 2, 10, 1004.00, 2) RETURNING id";
		row = jdbcTemplate.queryForRowSet(sql, id);
		row.next();
		long spaceId = row.getLong("id");

		// Test for within openDate range and under max occupancy
		startDate = LocalDate.of(2020, 3, 25);
		endDate = LocalDate.of(2020, 3, 28);
		numberOfAttendees = 2;
		spaces = dao.getAvailableSpacesForVenue(id, startDate, endDate, numberOfAttendees);
		Assert.assertEquals(1, spaces.size());

		// Test for before open date range
		startDate = LocalDate.of(2020, 1, 25);
		endDate = LocalDate.of(2020, 3, 28);
		numberOfAttendees = 2;
		spaces = dao.getAvailableSpacesForVenue(id, startDate, endDate, numberOfAttendees);
		Assert.assertEquals(0, spaces.size());

		// Test for after open date range
		startDate = LocalDate.of(2020, 7, 25);
		endDate = LocalDate.of(2020, 11, 28);
		numberOfAttendees = 2;
		spaces = dao.getAvailableSpacesForVenue(id, startDate, endDate, numberOfAttendees);
		Assert.assertEquals(0, spaces.size());

		// Test for over max occupancy
		startDate = LocalDate.of(2020, 3, 25);
		endDate = LocalDate.of(2020, 3, 28);
		numberOfAttendees = 3;
		spaces = dao.getAvailableSpacesForVenue(id, startDate, endDate, numberOfAttendees);
		Assert.assertEquals(0, spaces.size());

		// Add reservation
		startDate = LocalDate.of(2020, 3, 25);
		endDate = LocalDate.of(2020, 3, 28);
		numberOfAttendees = 1;
		sql = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, "
				+ "reserved_for) VALUES (DEFAULT, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(sql, spaceId, numberOfAttendees, startDate, endDate, "Johnny Appleseed");

		// Test for conflicting with existing reservation
		startDate = LocalDate.of(2020, 3, 23);
		endDate = LocalDate.of(2020, 3, 26);
		numberOfAttendees = 1;
		spaces = dao.getAvailableSpacesForVenue(id, startDate, endDate, numberOfAttendees);
		Assert.assertEquals(0, spaces.size());

		// Test for not conflicting with existing reservation
		startDate = LocalDate.of(2020, 3, 29);
		endDate = LocalDate.of(2020, 3, 30);
		numberOfAttendees = 1;
		spaces = dao.getAvailableSpacesForVenue(id, startDate, endDate, numberOfAttendees);
		Assert.assertEquals(1, spaces.size());

		// Add over 5 spaces
		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Mars Rover2', "
				+ "true, 2, 10, 1004.00, 2)";
		jdbcTemplate.update(sql, id);

		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Mars Rover3', "
				+ "true, 2, 10, 1004.00, 2)";
		jdbcTemplate.update(sql, id);

		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Mars Rover4', "
				+ "true, 2, 10, 1004.00, 2)";
		jdbcTemplate.update(sql, id);

		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Mars Rover5', "
				+ "true, 2, 10, 1004.00, 2)";
		jdbcTemplate.update(sql, id);

		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Mars Rover6', "
				+ "true, 2, 10, 1004.00, 2)";
		jdbcTemplate.update(sql, id);

		// Test for only returns 5 possible spaces
		startDate = LocalDate.of(2020, 3, 29);
		endDate = LocalDate.of(2020, 3, 30);
		numberOfAttendees = 1;
		spaces = dao.getAvailableSpacesForVenue(id, startDate, endDate, numberOfAttendees);
		Assert.assertEquals(5, spaces.size());

	}

	private void truncateSpace() {
		String sql = "TRUNCATE space CASCADE";
		jdbcTemplate.update(sql);
	}

}
