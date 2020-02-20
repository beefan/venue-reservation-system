package com.techelevator.excelsior.model.dao.jdbc;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.excelsior.model.Space;
import com.techelevator.excelsior.model.dao.SpaceDAO;

public class JDBCSpaceDAO implements SpaceDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCSpaceDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Space> getAvailableSpaces(LocalDate startDate, LocalDate endDate, int numberOfAttendees) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Space> getSpacesByVenueId(long venueId) {
		// TODO Auto-generated method stub
		return null;
	}

}
