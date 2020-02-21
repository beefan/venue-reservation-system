package com.techelevator.excelsior.model.dao;

import java.time.LocalDate;
import java.util.List;

import com.techelevator.excelsior.model.Reservation;
import com.techelevator.excelsior.model.Space;

public interface ReservationDAO {

	public void addReservation(long spaceId, int numberOfAttendees, LocalDate startDate, LocalDate endDate,
			String reservedFor);

	public List<Reservation> searchReservations(long spaceId, LocalDate startDate, LocalDate endDate);

	public List<Space> getAvailableSpaces(List<Space> spaces, LocalDate startDate, LocalDate endDate,
			int numberOfAteendees);

	// TODO Bonus Upcoming Reservation List - next 30 days
	// public List <Reservation> getUpcomingReservations();
}
