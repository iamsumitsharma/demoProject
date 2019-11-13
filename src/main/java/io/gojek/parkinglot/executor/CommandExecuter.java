/**
 * 
 */
package io.gojek.parkinglot.executor;


import java.util.Arrays;
import java.util.List;


import io.gojek.parkinglot.constants.Constants;

import io.gojek.parkinglot.service.ParkingService;



public interface CommandExecuter
{
	public void setService(ParkingService service);
	
	public void execute(String action) throws Exception;
	
	public default boolean validate(String inputString)
	{
		String[] inputs = inputString.split(" ");
		String[] allCmds = new String[]{Constants.CREATE_PARKING_LOT,Constants.PARK, Constants.LEAVE, Constants.STATUS, Constants.REG_NUMBER_FOR_CARS_WITH_COLOR, Constants.SLOTS_NUMBER_FOR_CARS_WITH_COLOR, Constants.SLOTS_NUMBER_FOR_REG_NUMBER};				
		List<String> list = Arrays.asList(allCmds);
		System.out.println("cmd" + inputs[0] + list.contains(inputs[0]));
		return list.contains(inputs[0]);
	}
}
