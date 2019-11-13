/**
 * 
 */
package io.gojek.parkinglot.parkingobjecthandlers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.gojek.parkinglot.constants.Constants;
import io.gojek.parkinglot.parkingobjecthandlers.ParkingLevelDataManager;
import io.gojek.parkinglot.model.Vehicle;
import io.gojek.parkinglot.model.SequentialParkingHandler;
import io.gojek.parkinglot.model.ParkingMethods;


public class ParkingObjectManipulator<T extends Vehicle> implements ParkingLevelDataManager<T>
{
	
	private AtomicInteger	level			= new AtomicInteger(0);
	private AtomicInteger	capacity		= new AtomicInteger();
	private AtomicInteger	availability	= new AtomicInteger();

	private ParkingMethods parkingStrategy;

	private Map<Integer, Optional<T>> vehicleSlotMapping;
	
	@SuppressWarnings("rawtypes")
	private static ParkingObjectManipulator parkingObjectHolder = null;
	
	@SuppressWarnings("unchecked")
	public static <T extends Vehicle> ParkingObjectManipulator<T> getInstance(int level, int capacity,
			ParkingMethods parkingStrategy)
	{
		if (parkingObjectHolder == null)
		{
	
			parkingObjectHolder = new ParkingObjectManipulator<T>(level, capacity, parkingStrategy);

		}
		return parkingObjectHolder;
	}
	
	private ParkingObjectManipulator(int level, int capacity, ParkingMethods parkingStrategy)
	{
		this.level.set(level);
		this.capacity.set(capacity);
		this.availability.set(capacity);
		this.parkingStrategy = parkingStrategy;
		if (parkingStrategy == null)
			parkingStrategy = new SequentialParkingHandler();
		vehicleSlotMapping = new ConcurrentHashMap<>();
		for (int i = 1; i <= capacity; i++)
		{
			vehicleSlotMapping.put(i, Optional.empty());
			parkingStrategy.add(i);
		}
	}
	
	@Override
	public int parkCar(T vehicle)
	{
		int availableSlot;
		if (availability.get() == 0)
		{
			return Constants.NOT_AVAILABLE;
		}
		else
		{
			availableSlot = parkingStrategy.getSlot();
			if (vehicleSlotMapping.containsValue(Optional.of(vehicle)))
				return Constants.VEHICLE_ALREADY_EXIST;
			
			vehicleSlotMapping.put(availableSlot, Optional.of(vehicle));
			availability.decrementAndGet();
			parkingStrategy.removeSlot(availableSlot);
		}
		return availableSlot;
	}
	
	@Override
	public boolean leaveCar(int slotNumber)
	{
		
		if (!vehicleSlotMapping.get(slotNumber).isPresent()) 
			return false;
		availability.incrementAndGet();
		parkingStrategy.add(slotNumber);
		// Duration timeSpentPark = Duration.between(LocalDateTime.now(), vehicleSlotMapping.get(slotNumber).get().getEntryDateTime());
		// long totalFare = Math.abs(timeSpentPark.toMinutes());
		// System.out.println("Total fare" + 3*totalFare);
		vehicleSlotMapping.put(slotNumber, Optional.empty());
		return true;
	}
	
	@Override
	public List<String> getStatus()
	{
		List<String> statusList = new ArrayList<>();
		for (int i = 1; i <= capacity.get(); i++)
		{
			Optional<T> vehicle = vehicleSlotMapping.get(i);
			if (vehicle.isPresent())
			{
				// vehicle.get().getEntryDateTime() can also be added along with this so as to maintain track of time spent
				statusList.add(i + "\t\t" + vehicle.get().getVehicleNumber() + "\t\t" + vehicle.get().getVehicleColor());
			}
		}
		return statusList;
	}
	
	public int getAvailableSlotsCount()
	{
		return availability.get();
	}
	
	@Override
	public List<String> getRegNumberForColor(String color)
	{
		List<String> statusList = new ArrayList<>();
		for (int i = 1; i <= capacity.get(); i++)
		{
			Optional<T> vehicle = vehicleSlotMapping.get(i);
			if (vehicle.isPresent() && color.equalsIgnoreCase(vehicle.get().getVehicleColor()))
			{
				statusList.add(vehicle.get().getVehicleNumber());
			}
		}
		return statusList;
	}
	
	@Override
	public List<Integer> getSlotNumbersFromColor(String colour)
	{
		List<Integer> slotList = new ArrayList<>();
		for (int i = 1; i <= capacity.get(); i++)
		{
			Optional<T> vehicle = vehicleSlotMapping.get(i);
			if (vehicle.isPresent() && colour.equalsIgnoreCase(vehicle.get().getVehicleColor()))
			{
				slotList.add(i);
			}
		}
		return slotList;
	}
	
	@Override
	public int getSlotNoFromRegistrationNo(String registrationNo)
	{
		int result = Constants.NOT_FOUND;
		for (int i = 1; i <= capacity.get(); i++)
		{
			Optional<T> vehicle = vehicleSlotMapping.get(i);
			if (vehicle.isPresent() && registrationNo.equalsIgnoreCase(vehicle.get().getVehicleNumber()))
			{
				result = i;
			}
		}
		return result;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	@Override
	public void doCleanUp()
	{
		this.level = new AtomicInteger();
		this.capacity = new AtomicInteger();
		this.availability = new AtomicInteger();
		this.parkingStrategy = null;
		vehicleSlotMapping = null;
		parkingObjectHolder = null;
	}
}
