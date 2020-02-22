package com.techelevator.excelsior.model.dao.jdbc;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.excelsior.model.Space;
import com.techelevator.excelsior.model.dao.SpaceDAO;

public class JDBCSpaceDAO implements SpaceDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCSpaceDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Space> getAvailableSpaces(LocalDate startDate, LocalDate endDate, int numberOfAttendees,
			boolean isAccesible, double dailyRate) {
		// TODO Bonus search
		return null;
	}

	@Override
	public List<Space> getSpacesByVenueId(long venueId) {
		List<Space> space = new LinkedList<Space>();

		String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate::decimal, max_occupancy "
				+ "FROM space WHERE venue_id = ?";
		SqlRowSet spaceResults = jdbcTemplate.queryForRowSet(sql, venueId);

		while (spaceResults.next()) {
			space.add(mapRowToSpace(spaceResults));
		}

		return space;
	}

	@Override
	public List<Space> getAvailableSpacesForVenue(long venueId, LocalDate startDate, LocalDate endDate,
			int numberOfAttendees) {
		List<Space> space = new LinkedList<Space>();
		int startMonth = startDate.getMonthValue();
		int endMonth = endDate.getMonthValue();

		String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, "
				+ "daily_rate::decimal, max_occupancy FROM space WHERE venue_id = ? AND (((? BETWEEN "
				+ "open_from AND open_to) AND (? BETWEEN open_from AND open_to) ) OR open_from IS NULL) "
				+ "AND id NOT IN ( SELECT DISTINCT space_id FROM reservation WHERE "
				+ "( ? BETWEEN start_date AND end_date ) OR (? BETWEEN start_date AND end_date)) "
				+ "AND ? <= max_occupancy LIMIT 5";

		SqlRowSet spaceResults = jdbcTemplate.queryForRowSet(sql, venueId, startMonth, endMonth, startDate, endDate,
				numberOfAttendees);

		while (spaceResults.next()) {
			space.add(mapRowToSpace(spaceResults));
		}

		return space;
	}

	private Space mapRowToSpace(SqlRowSet result) {
		Space space = new Space();

		space.setId(result.getLong("id"));
		space.setVenueId(result.getLong("venue_id"));
		space.setName(result.getString("name"));
		space.setAccessible(result.getBoolean("is_accessible"));
		space.setOpenFrom(result.getInt("open_from"));
		space.setOpenTo(result.getInt("open_to"));
		space.setDailyRate(result.getDouble("daily_rate"));
		space.setMaxOccupancy(result.getInt("max_occupancy"));

		return space;
	}

}
