/**
 * 
 */
package io.gojek.parkinglot.service;

import java.util.Optional;



import io.gojek.parkinglot.model.Vehicle;



public interface ParkingService
{
	
	public void createParkingLot(int level, int capacity) throws Exception;
	
	public Optional<Integer> park(int level, Vehicle vehicle) throws Exception;
	
	public void unPark(int level, int slotNumber) throws Exception;
	
	public void getStatus(int level) throws Exception;
	
	public Optional<Integer> getAvailableSlotsCount(int level) throws Exception;
	
	public void getRegNumberForColor(int level, String color) throws Exception;
	
	public void getSlotNumbersFromColor(int level, String colour) throws Exception;
	
	public int getSlotNoFromRegistrationNo(int level, String registrationNo) throws Exception;
	
	public void doCleanup();
}
