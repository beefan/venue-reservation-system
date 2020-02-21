package com.techelevator.excelsior.model.dao.jdbc;

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
	public void get_available_spaces_by_venue_id() {
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

	private void truncateSpace() {
		String sql = "TRUNCATE space CASCADE";
		jdbcTemplate.update(sql);
	}

}