package com.techelevator.excelsior.model.dao;

import java.time.LocalDate;
import java.util.List;

import com.techelevator.excelsior.model.Space;

public interface SpaceDAO {

	public List<Space> getSpacesByVenueId(long venueId);

	public List<Space> getAvailableSpaces(LocalDate startDate, LocalDate endDate, int numberOfAttendees,
			boolean isAccesible, double dailyRate);

	// TODO Bonus Override for Advanced Search
	// public List<Space> getAvailableSpaces(LocalDate startDate, LocalDate endDate,
	// int numberOfAttendees, double dailyPrice, boolean isAccessible, long
	// categoryId);

}
