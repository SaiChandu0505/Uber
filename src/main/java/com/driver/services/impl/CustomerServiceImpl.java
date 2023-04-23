package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		List<Driver> drivers = driverRepository2.findAll();

		Collections.sort(drivers, (a, b)->a.getDriverId()-b.getDriverId());
		Driver getDriver = null;
		for(Driver driver: drivers)
			if (driver.getCab().getAvailable()) {
				getDriver = driver;
				break;
			}
		if(getDriver==null){
			throw new Exception("No cab available!");
		}

		TripBooking newTrip = new TripBooking();
		newTrip.setCustomer(customerRepository2.findById(customerId).get());
		newTrip.setDriver(getDriver);
		newTrip.setFromLocation(fromLocation);
		newTrip.setToLocation(toLocation);
		newTrip.setDistanceInKm(distanceInKm);
		newTrip.setBill(getDriver.getCab().getPerKmRate()*distanceInKm);
		newTrip.setStatus(TripStatus.CONFIRMED);

		getDriver.getTripBookingList().add(newTrip);
		driverRepository2.save(getDriver);


		Customer customer = customerRepository2.findById(customerId).get();
		customer.getTripBookings().add(newTrip);
		customerRepository2.save(customer);

		tripBookingRepository2.save(newTrip);
		return newTrip;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly

		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.CANCELED);
		trip.getDriver().getCab().setAvailable(true);
		trip.setBill(0);
		trip.setToLocation(null);
		trip.setFromLocation(null);
		trip.setDistanceInKm(0);
		tripBookingRepository2.save(trip);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking trip = tripBookingRepository2.findById(tripId).get();
		trip.setStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(trip);
	}
}