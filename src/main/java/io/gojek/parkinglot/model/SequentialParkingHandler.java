/**
 * 
 */
package io.gojek.parkinglot.model;

import java.util.HashSet;
import java.util.Set;


public class SequentialParkingHandler implements ParkingMethods
{
	private Set<Integer> freeSlots;
	
	public SequentialParkingHandler()
	{
		freeSlots = new HashSet<Integer>();
	}
	
	@Override
	public void add(int i)
	{
		freeSlots.add(i);
	}
	
	@Override
	public int getSlot()
	{
		return freeSlots.stream().findFirst().get();
	}
	
	@Override
	public void removeSlot(int availableSlot)
	{
		freeSlots.remove(availableSlot);
	}
}
