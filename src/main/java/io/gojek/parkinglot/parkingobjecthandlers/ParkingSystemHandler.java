/**
 * 
 */
package io.gojek.parkinglot.parkingobjecthandlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.gojek.parkinglot.parkingobjecthandlers.ParkingInteractionInterface;
import io.gojek.parkinglot.parkingobjecthandlers.ParkingLevelDataManager;
import io.gojek.parkinglot.model.Vehicle;
import io.gojek.parkinglot.model.SequentialParkingHandler;
import io.gojek.parkinglot.model.ParkingMethods;


public class ParkingSystemHandler<T extends Vehicle> implements ParkingInteractionInterface<T>
{
	private Map<Integer, ParkingLevelDataManager<T>> levelParkingMap;
	
	@SuppressWarnings("rawtypes")
	private static ParkingSystemHandler instance = null;
	
	@SuppressWarnings("unchecked")
	public static <T extends Vehicle> ParkingSystemHandler<T> getInstance(List<Integer> parkingLevels,
			List<Integer> capacityList, List<ParkingMethods> parkingStrategies)
	{
		if (instance == null)
		{
		
			instance = new ParkingSystemHandler<T>(parkingLevels, capacityList, parkingStrategies);
		}
		return instance;
	}
	
	private ParkingSystemHandler(List<Integer> parkingLevels, List<Integer> capacityList,
			List<ParkingMethods> parkingStrategies)
	{
		if (levelParkingMap == null)
			levelParkingMap = new HashMap<>();
		for (int i = 0; i < parkingLevels.size(); i++)
		{
			levelParkingMap.put(parkingLevels.get(i), ParkingObjectManipulator.getInstance(parkingLevels.get(i),
					capacityList.get(i), new SequentialParkingHandler()));
		}
	}
	
	@Override
	public int parkCar(int level, T vehicle)
	{
		return levelParkingMap.get(level).parkCar(vehicle);
	}
	
	@Override
	public boolean leaveCar(int level, int slotNumber)
	{
		return levelParkingMap.get(level).leaveCar(slotNumber);
	}
	
	@Override
	public List<String> getStatus(int level)
	{
		return levelParkingMap.get(level).getStatus();
	}
	
	public int getAvailableSlotsCount(int level)
	{
		return levelParkingMap.get(level).getAvailableSlotsCount();
	}
	
	@Override
	public List<String> getRegNumberForColor(int level, String color)
	{
		return levelParkingMap.get(level).getRegNumberForColor(color);
	}
	
	@Override
	public List<Integer> getSlotNumbersFromColor(int level, String color)
	{
		return levelParkingMap.get(level).getSlotNumbersFromColor(color);
	}
	
	@Override
	public int getSlotNoFromRegistrationNo(int level, String registrationNo)
	{
		return levelParkingMap.get(level).getSlotNoFromRegistrationNo(registrationNo);
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	public void doCleanup()
	{
		for (ParkingLevelDataManager<T> levelDataManager : levelParkingMap.values())
		{
			levelDataManager.doCleanUp();
		}
		levelParkingMap = null;
		instance = null;
	}
}
