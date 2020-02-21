package com.techelevator.excelsior.model.dao.jdbc;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.DAOIntegrationTest;

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

		dao.addReservation(spaceId, numberOfAttendees, startDate, endDate, reservedFor);

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

	}

	private void truncateReservation() {
		String sql = "TRUNCATE reservation CASCADE";
		jdbcTemplate.update(sql);
	}

}
