import org.apache.log4j.Logger;

import GUI.transportationGUI.GUI;
import beans.Building;
import beans.Constants;
import beans.Elevator;
import beans.Passenger;
import beans.PassengersList;
import beans.Reader;
import beans.Story;



public class Runner {
	private static Logger logger = Logger.getLogger(Runner.class);
	

	public static void main(String[] args){
		final String FILE_NAME = "config";
        final int MIN_STORIES_NUMBER = 2; 
        final int MIN_ELEVATOR_CAPACITY = 1;	
        final int MIN_PASSENGER_NUMBER = 1;
        int storiesNumber;
        int	elevatorCapacity;
        int	passengersNumber;
        int	animationBoost;
       
        
		Reader reader = new Reader(FILE_NAME);
		
		storiesNumber = reader.getValue(Constants.KEY_STORIES_NUMBER);
		elevatorCapacity = reader.getValue(Constants.KEY_ELEVATOR_CAPACITY);
		passengersNumber = reader.getValue(Constants.KEY_PASSENGERS_NUMBER);
		animationBoost = reader.getValue(Constants.KEY_ANIMATION_BOOST);
		
		
		if ((storiesNumber < MIN_STORIES_NUMBER )|(elevatorCapacity < MIN_ELEVATOR_CAPACITY)|(passengersNumber < MIN_PASSENGER_NUMBER)) {
			logger.error(Constants.ERROR_DATA);
			System.exit(1);
		}
		 
		Elevator elevator = new Elevator(elevatorCapacity);
		Building building = new Building(storiesNumber,elevator);
		
		GUI gui = new GUI(building,animationBoost);
		elevator.getController().setGui(gui);
		
		Thread guiThread = new Thread(gui);
		guiThread.start();
		
		while (!guiThread.getState().equals(Thread.State.TERMINATED)){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
			}
		}
		
		PassengersList allPassengers = new PassengersList();
		
		for (int i = 0; i < passengersNumber;i++) {
        	
        	Passenger passenger = new Passenger(building,gui);
        	allPassengers.addPassenger(passenger);
 
        	for (Story st: building.getStories()) {	
        		if (st.equals(passenger.getDispatchStory())) {
        			st.getDispatchStoryContainer().addPassenger(passenger);
        		}
        	}
    	}
   
		if (animationBoost > 0) {
			while (!gui.getStartState()) {
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
				}
			}
		
		} else {
			gui.getGuiBuild().getTimer().start();
		}
		
		for (Passenger passenger: allPassengers.getContainer()) {
			Thread transportationTask = new Thread(passenger.createTransportationTask());
        	transportationTask.start();
        }
	   
		elevator.getController().transport(building.getStories(),allPassengers);     
		gui.getGuiBuild().getTimer().stop();
	}
}


	
	
	
	
	


