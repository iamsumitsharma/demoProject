/**
 * 
 */
package io.gojek.parkinglot.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;;

@Data
public abstract class Vehicle implements Externalizable
{
	private String	vehicleNumber	= null;
	private String	vehicleColor	= null;
	private LocalDateTime entryDateTime = LocalDateTime.now();
	public Vehicle(String registrationNo, String color)
	{
		this.vehicleNumber = registrationNo;
		this.vehicleColor = color;
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeObject(getVehicleNumber());
		out.writeObject(getVehicleColor());
		out.writeObject(getEntryDateTime());
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		setVehicleNumber((String) in.readObject());
		setVehicleColor((String) in.readObject());
		setEntryDateTime(this.entryDateTime);
	}
}
