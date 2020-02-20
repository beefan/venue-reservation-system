package com.techelevator.excelsior.model.dao;

import java.util.List;

import com.techelevator.excelsior.model.Venue;
import com.techelevator.excelsior.model.dao.jdbc.JDBCSpaceDAO;

public interface VenueDAO {

	// TODO Sort Alphabetically
	public List<Venue> getVenues(JDBCSpaceDAO spaceDAO);

}
