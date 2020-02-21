package com.techelevator.excelsior.model.dao.jdbc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.excelsior.model.Venue;
import com.techelevator.excelsior.model.dao.VenueDAO;

public class JDBCVenueDAO implements VenueDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCVenueDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Venue> getVenues(JDBCSpaceDAO spaceDAO) {

		List<Venue> venue = new LinkedList<Venue>();

		String sql = "SELECT venue.id AS venue_id, venue.name AS venue_name, city.name AS city_name, "
				+ "state.name AS state_name, venue.description AS venue_description FROM venue "
				+ "JOIN city ON venue.city_id = city.id "
				+ "JOIN state ON city.state_abbreviation = state.abbreviation ORDER BY venue_name";
		SqlRowSet venueResults = jdbcTemplate.queryForRowSet(sql);

		while (venueResults.next()) {
			venue.add(mapRowToVenue(venueResults));
		}

		for (Venue oneVenue : venue) {
			sql = "SELECT category.name AS category_name FROM category "
					+ "JOIN category_venue ON category.id = category_venue.category_id "
					+ "WHERE category_venue.venue_id = ?";
			SqlRowSet categoryResults = jdbcTemplate.queryForRowSet(sql, oneVenue.getId());
			addCategoriesToVenue(categoryResults, oneVenue);

			oneVenue.setSpaces(spaceDAO.getSpacesByVenueId(oneVenue.getId()));
		}

		return venue;
	}

	private Venue mapRowToVenue(SqlRowSet result) {
		Venue venue = new Venue();

		venue.setId(result.getLong("venue_id"));
		venue.setName(result.getString("venue_name"));
		venue.setCityName(result.getString("city_name"));
		venue.setStateName(result.getString("state_name"));
		venue.setDescription(result.getString("venue_description"));
		return venue;
	}

	private void addCategoriesToVenue(SqlRowSet result, Venue venue) {
		List<String> categories = new ArrayList<>();
		while (result.next()) {
			categories.add(result.getString("category_name"));
		}
		venue.setCategories(categories);
	}

}
