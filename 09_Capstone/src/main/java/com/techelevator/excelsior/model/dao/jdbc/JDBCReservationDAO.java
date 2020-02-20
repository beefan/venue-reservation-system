package com.techelevator.excelsior.model.dao.jdbc;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.excelsior.model.Reservation;
import com.techelevator.excelsior.model.dao.ReservationDAO;

public class JDBCReservationDAO implements ReservationDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCReservationDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void addReservation(long spaceId, int numberOfAttendees, LocalDate startDate, LocalDate endDate,
			String reservedFor) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Reservation> getAvailableReservations(long spaceId, LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
