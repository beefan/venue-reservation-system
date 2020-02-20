package com.techelevator.excelsior;

import java.util.List;

import javax.sql.DataSource;

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

}
