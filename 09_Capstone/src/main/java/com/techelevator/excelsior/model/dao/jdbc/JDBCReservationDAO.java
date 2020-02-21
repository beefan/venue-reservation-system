package com.techelevator.excelsior.model.dao.jdbc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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
		String sql = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, "
				+ "reserved_for) VALUES (DEFAULT, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(sql, spaceId, numberOfAttendees, startDate, endDate, reservedFor);

	}

	@Override
	public List<Reservation> searchReservations(long spaceId, LocalDate startDate, LocalDate endDate) {
		List<Reservation> reservation = new LinkedList<Reservation>();

		String sql = "SELECT reservation_id, space.name AS space_name, venue.name AS venue_name, "
				+ "space.daily_rate AS daily_rate, number_of_attendees, start_date, end_date, reserved_for "
				+ "FROM reservation JOIN space ON reservation.space_id = space.id "
				+ "JOIN venue ON space.venue_id = venue.id " + "WHERE space_id = ? AND (start_date BETWEEN ? AND ?) "
				+ "AND (end_date BETWEEN ? AND ?)";
		SqlRowSet reservationResults = jdbcTemplate.queryForRowSet(sql, spaceId, startDate, endDate, startDate,
				endDate);

		while (reservationResults.next()) {
			reservation.add(mapRowToReservation(reservationResults));
		}

		return reservation;
	}

	private Reservation mapRowToReservation(SqlRowSet result) {
		Reservation reservation = new Reservation();

		LocalDate startDate = result.getDate("start_date").toLocalDate();
		LocalDate endDate = result.getDate("end_date").toLocalDate();
		double totalCost = result.getDouble("daily_rate") * (ChronoUnit.DAYS.between(startDate, endDate));

		reservation.setId(result.getLong("reservation_id"));
		reservation.setVenue(result.getString("venue_name"));
		reservation.setSpace(result.getString("space_name"));
		reservation.setTotalCost(totalCost);
		reservation.setStartDate(startDate);
		reservation.setEndDate(endDate);
		reservation.setNumberOfAttendees(result.getInt("number_of_attendees"));
		reservation.setReservedFor(result.getString("reserved_for"));

		return reservation;
	}

}
