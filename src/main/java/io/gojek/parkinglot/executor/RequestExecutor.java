/**
 * 
 */
package io.gojek.parkinglot.executor;

import io.gojek.parkinglot.constants.Constants;
import io.gojek.parkinglot.exception.ErrorCode;
import io.gojek.parkinglot.exception.ParkingException;
import io.gojek.parkinglot.model.Car;
import io.gojek.parkinglot.executor.*;
import io.gojek.parkinglot.service.ParkingService;;



public class RequestExecutor implements CommandExecuter
{
	private ParkingService parkingService;
	
	public void setParkingService(ParkingService parkingService) throws Exception
	{
		this.parkingService = parkingService;
	}
	
	@Override
	public void execute(String input) throws Exception
	{
		int level = 1;
		String[] inputs = input.split(" ");
		String key = inputs[0];
		switch (key)
		{
			case Constants.CREATE_PARKING_LOT:
				try
				{
					int capacity = Integer.parseInt(inputs[1]);
					// parkingService.createParkingLot(level, capacity).subscribeOn(Schedulers.io());
					parkingService.createParkingLot(level, capacity);
				}
				catch (Exception e)
				{
					throw new ParkingException(ErrorCode.INVALID_VALUE.getMessage());
				}
				break;
			case Constants.PARK:
				parkingService.park(level, new Car(inputs[1], inputs[2]));
				break;
			case Constants.LEAVE:
				try
				{
					int slotNumber = Integer.parseInt(inputs[1]);
					parkingService.unPark(level, slotNumber);
				}
				catch (Exception e)
				{
					throw new ParkingException(
							ErrorCode.INVALID_VALUE.getMessage().replace("{variable}", "slot_number"));
				}
				break;
			case Constants.STATUS:
				parkingService.getStatus(level);
				break;
			case Constants.REG_NUMBER_FOR_CARS_WITH_COLOR:
				parkingService.getRegNumberForColor(level, inputs[1]);
				break;
			case Constants.SLOTS_NUMBER_FOR_CARS_WITH_COLOR:
				parkingService.getSlotNumbersFromColor(level, inputs[1]);
				break;
			case Constants.SLOTS_NUMBER_FOR_REG_NUMBER:
				parkingService.getSlotNoFromRegistrationNo(level, inputs[1]);
				break;
			default:
				break;
		}
	}
	
	@Override
	public void setService(ParkingService service)
	{
		this.parkingService = (ParkingService) service;
	}
}
