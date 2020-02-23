package com.techelevator.excelsior.model.dao.jdbc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.DAOIntegrationTest;
import com.techelevator.excelsior.model.Space;
import com.techelevator.excelsior.model.Venue;

public class JDBCVenueDAOTest extends DAOIntegrationTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCVenueDAO dao;
	private JdbcTemplate jdbcTemplate;
	private JDBCSpaceDAO spaceDao;
	private JDBCCategoryDAO categoryDao;

	@Before
	public void setup() {
		dataSource = (SingleConnectionDataSource) super.getDataSource();
		dao = new JDBCVenueDAO(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
		spaceDao = new JDBCSpaceDAO(dataSource);
		categoryDao = new JDBCCategoryDAO(dataSource);
	}

	@Test
	public void get_all_venues() {
		truncateVenue();

		// test empty table returns 0 size
		List<Venue> venues = new LinkedList<Venue>();
		venues = dao.getVenues(spaceDao, categoryDao);
		Assert.assertEquals(0, venues.size());

		// add venue
		String sql = "INSERT INTO venue (id, name, city_id, description) VALUES "
				+ "(DEFAULT, 'The Stinky Skunk', 2, 'Super smelly place')";
		jdbcTemplate.update(sql);

		venues = dao.getVenues(spaceDao, categoryDao);

		// test for added venue
		Assert.assertEquals(1, venues.size());
		Assert.assertEquals("Srulbury", venues.get(0).getCityName());
		Assert.assertEquals("The Stinky Skunk", venues.get(0).getName());
		Assert.assertEquals("Super smelly place", venues.get(0).getDescription());
		Assert.assertEquals(new ArrayList<String>(), venues.get(0).getCategories());
		Assert.assertEquals(new ArrayList<Space>(), venues.get(0).getSpaces());
		// Assert.assertNull(venues.get(0).getSpaces());

		// add a second venue
		sql = "INSERT INTO venue (id, name, city_id, description) VALUES "
				+ "(DEFAULT, 'The Singing Belle', 3, 'Super sound spot') RETURNING id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		row.next();
		long id = row.getLong("id");
		venues = dao.getVenues(spaceDao, categoryDao);

		// Test for alphabetical order
		Assert.assertEquals("The Singing Belle", venues.get(0).getName());
		Assert.assertEquals("The Stinky Skunk", venues.get(1).getName());

		// add one category to second venue
		sql = "INSERT INTO category_venue (venue_id, category_id) VALUES (?, 6)";
		jdbcTemplate.update(sql, id);

		venues = dao.getVenues(spaceDao, categoryDao);

		// Test for added category
		Assert.assertEquals("Modern", venues.get(0).getCategories().get(0));

		// add one category to second venue
		sql = "INSERT INTO category_venue (venue_id, category_id) VALUES " + "(?, 4)";
		jdbcTemplate.update(sql, id);

		venues = dao.getVenues(spaceDao, categoryDao);

		// Test for added category
		Assert.assertEquals("Rustic", venues.get(0).getCategories().get(0));
		Assert.assertEquals("Modern", venues.get(0).getCategories().get(1));
		Assert.assertEquals(2, venues.get(0).getCategories().size());

		// add one space
		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'Mars Rover', " + "true, 2, 10, 1004.00, 2)";
		jdbcTemplate.update(sql, id);

		venues = dao.getVenues(spaceDao, categoryDao);

		// Test for added space
		Assert.assertEquals("Mars Rover", venues.get(0).getSpaces().get(0).getName());

		sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, "
				+ "open_to, daily_rate, max_occupancy) VALUES (DEFAULT, ?, 'International Station', "
				+ "true, DEFAULT, DEFAULT, 100007.00, 12)";
		jdbcTemplate.update(sql, id);

		venues = dao.getVenues(spaceDao, categoryDao);

		// Test for added space
		Assert.assertEquals("International Station", venues.get(0).getSpaces().get(1).getName());
		Assert.assertEquals("Mars Rover", venues.get(0).getSpaces().get(0).getName());
		Assert.assertEquals(2, venues.get(0).getSpaces().size());

		// Test for updated size
		Assert.assertEquals(2, venues.size());

	}

	private void truncateVenue() {
		String sql = "TRUNCATE venue CASCADE";
		jdbcTemplate.update(sql);
	}

}
