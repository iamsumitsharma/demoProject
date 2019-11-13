/**
 * 
 */
package io.gojek.parkinglot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;


import io.gojek.parkinglot.constants.Constants;
import io.gojek.parkinglot.parkingobjecthandlers.ParkingInteractionInterface;
import io.gojek.parkinglot.parkingobjecthandlers.ParkingSystemHandler;
import io.gojek.parkinglot.exception.ErrorCode;
import io.gojek.parkinglot.exception.ParkingException;
import io.gojek.parkinglot.model.Vehicle;
import io.gojek.parkinglot.model.SequentialParkingHandler;
import io.gojek.parkinglot.model.ParkingMethods;
import io.gojek.parkinglot.service.ParkingService;
import io.reactivex.Single;
import io.reactivex.*;

public class ParkingServiceImpl implements ParkingService
{
	private ParkingInteractionInterface<Vehicle> parkingHandler = null;

	interface parkingLotGenericInterface {
		Integer parkingSlotId(String caseId, String errorType);
	}
	
	
	@Override
	public void createParkingLot(int level, int capacity) throws ParkingException
	{
		if (parkingHandler != null)
			throw new ParkingException(ErrorCode.PARKING_ALREADY_EXIST.getMessage());
	
		List<Integer> parkingLevels = new ArrayList<>();
		List<Integer> capacityList = new ArrayList<>();
		List<ParkingMethods> parkingStrategies = new ArrayList<>();
	
		parkingLevels.add(level);
		capacityList.add(capacity);
		parkingStrategies.add(new SequentialParkingHandler());
		this.parkingHandler = ParkingSystemHandler.getInstance(parkingLevels, capacityList, parkingStrategies);
		System.out.println("Created parking lot with " + capacity + " slots");
	}
	
	@Override
	public Optional<Integer> park(int level, Vehicle vehicle) throws ParkingException
	{
		Optional<Integer> value = Optional.empty();
		validateParkingLot();
		try
		{
			value = Optional.of(parkingHandler.parkCar(level, vehicle));
			switch(value.get()) {
				case Constants.NOT_AVAILABLE:
				System.out.println("Sorry, parking lot is full");
				break;
				case Constants.VEHICLE_ALREADY_EXIST:
				System.out.println("Sorry, vehicle is already parked.");
				break;
				default:
				System.out.println("Allocated slot number: " + value.get());
			}
		}
		catch (Exception e)
		{
			throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
		}
		return value;
	}
	
	/**
	 * @throws ParkingException
	 */
	private void validateParkingLot() throws ParkingException
	{
		if (parkingHandler == null)
		{
			throw new ParkingException(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage());
		}
	}
	
	@Override
	public void unPark(int level, int slotNumber) throws ParkingException
	{
		
		validateParkingLot();
		try
		{
			Optional.of(slotNumber).ifPresent(slot -> {
				if (parkingHandler.leaveCar(level, slot))
				System.out.println("Slot number " + slot + " is free");
			else
				System.out.println("Slot number is Empty Already.");
			});
		}
		catch (Exception e)
		{
			throw new ParkingException(ErrorCode.INVALID_VALUE.getMessage().replace("{variable}", "slot_number"), e);
		}
	}
	
	@Override
	public void getStatus(int level) throws ParkingException
	{
		
		validateParkingLot();
		try
		{
			System.out.println("Slot No.\tRegistration No.\tColor");
			List<String> statusList = parkingHandler.getStatus(level);
			if (statusList.size() == 0)
				System.out.println("Sorry, parking lot is empty.");
			else
			{
				statusList.stream().forEach(status -> System.out.println(status));
			}
		}
		catch (Exception e)
		{
			throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
		}
	}
	
	public Optional<Integer> getAvailableSlotsCount(int level) throws ParkingException
	{
		
		Optional<Integer> value = Optional.empty();
		validateParkingLot();
		try
		{
			value = Optional.of(parkingHandler.getAvailableSlotsCount(level));
		}
		catch (Exception e)
		{
			throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
		}
		return value;
	}
	
	@Override
	public void getRegNumberForColor(int level, String color) throws ParkingException
	{
		validateParkingLot();
		try
		{
			List<String> registrationList = parkingHandler.getRegNumberForColor(level, color);
			if (registrationList.size() == 0)
				System.out.println("Not Found");
			else
				System.out.println(String.join(",", registrationList));
		}
		catch (Exception e)
		{
			throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
		}
	}
	
	@Override
	public void getSlotNumbersFromColor(int level, String color) throws ParkingException
	{
		
		validateParkingLot();
		try
		{
			List<Integer> slotList = parkingHandler.getSlotNumbersFromColor(level, color);
			if (slotList.size() == 0)
				System.out.println("Not Found");
			StringJoiner joiner = new StringJoiner(",");
			for (Integer slot : slotList)
			{
				joiner.add(slot + "");
			}
			System.out.println(joiner.toString());
		}
		catch (Exception e)
		{
			throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
		}
	
	}
	
	@Override
	public int getSlotNoFromRegistrationNo(int level, String registrationNo) throws ParkingException
	{
		int value = -1;
		
		validateParkingLot();
		try
		{
			value = parkingHandler.getSlotNoFromRegistrationNo(level, registrationNo);
			System.out.println(value != -1 ? value : "Not Found");
		}
		catch (Exception e)
		{
			throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
		}
		finally
		{
			// lock.readLock().unlock();
		}
		return value;
	}
	
	@Override
	public void doCleanup()
	{
		if (parkingHandler != null)
			parkingHandler.doCleanup();
	}
}
