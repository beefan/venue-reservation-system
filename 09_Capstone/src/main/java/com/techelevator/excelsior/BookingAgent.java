package com.techelevator.excelsior;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.excelsior.model.Reservation;
import com.techelevator.excelsior.model.Space;
import com.techelevator.excelsior.model.Venue;
import com.techelevator.excelsior.model.dao.jdbc.JDBCReservationDAO;
import com.techelevator.excelsior.model.dao.jdbc.JDBCSpaceDAO;
import com.techelevator.excelsior.model.dao.jdbc.JDBCVenueDAO;

public class BookingAgent {

	JDBCVenueDAO jdbcVenueDAO;
	JDBCSpaceDAO jdbcSpaceDAO;
	JDBCReservationDAO jdbcReservationDAO;

	List<Venue> venues = null;

	public BookingAgent(DataSource datasource) {
		jdbcVenueDAO = new JDBCVenueDAO(datasource);
		jdbcSpaceDAO = new JDBCSpaceDAO(datasource);
		jdbcReservationDAO = new JDBCReservationDAO(datasource);
	}

	public List<Venue> getVenues() {
		if (venues == null) {
			venues = jdbcVenueDAO.getVenues(jdbcSpaceDAO);
		}
		return venues;
	}

	public List<Space> getAvailableSpacesForVenue(long venueId, LocalDate startDate, LocalDate endDate,
			int numberOfAttendees) {
		return jdbcSpaceDAO.getAvailableSpacesForVenue(venueId, startDate, endDate, numberOfAttendees);
	}

	public Reservation addReservation(long spaceId, int numberOfAttendees, LocalDate startDate, LocalDate endDate,
			String reservedFor) {
		return jdbcReservationDAO.addReservation(spaceId, numberOfAttendees, startDate, endDate, reservedFor);
	}

}
