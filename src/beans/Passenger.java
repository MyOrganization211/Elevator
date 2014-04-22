package beans;

import org.apache.log4j.Logger;

import Enums.Actions;
import Enums.Direction;
import Enums.TransportationState;
import GUI.transportationGUI.GUI;
import GUI.transportationGUI.MoveEnter;
import GUI.transportationGUI.MoveExit;


public class Passenger {
	
	private static int numberOfPassengers = 1;
	private int ID;
	private Building building;
	private GUI gui;
	private Story dispatchStory;  
	private Story destinationStory;
	private Direction direction; 
	private TransportationState transportationState = TransportationState.NOT_STARTED;
	private Thread threadEnter;
	private Thread threadExit;
	private StringBuilder message;
	private Logger logger = Logger.getLogger(Passenger.class);
	private boolean isNotTriedEnter = true; 
	private boolean isNotTriedExit = true; 
	private boolean isReadyTransport = false; 
		
	
	

	public Passenger(Building building,GUI gui){
		this.building = building; 
		this.gui = gui;
		this.ID = numberOfPassengers++;
		initDispatchStory();
		initDestinationStory();
		initDirection();
		threadExit = new Thread(new MoveExit(gui)); 
		threadEnter =  new Thread(new MoveEnter
				(gui));
		
	}
	

	public void setReadyTransport(boolean isReadyTransport){
		this.isReadyTransport = isReadyTransport;
	}

	public GUI getGui(){
		return gui;
	}

	public void setGui(GUI gui){
		this.gui = gui;
	}

	public Building getBuilding(){
		return building;
	}

	public int getID(){
		return ID;
	}

	public Story getDispatchStory(){
		return dispatchStory;
	}


	public Story getDestinationStory(){
		return destinationStory;
	}

	public boolean isNotTriedEnter(){
		return isNotTriedEnter;
	}

	public void setNotTriedEnter(boolean isNotTriedEnter){
		this.isNotTriedEnter = isNotTriedEnter;
	}

	public boolean isNotTriedExit(){
		return isNotTriedExit;
	}
	
	public void setNotTriedExit(boolean isNotTriedExit){
		this.isNotTriedExit = isNotTriedExit;
	}

	private int initNumberStory(){
		int numberStory = (int)(Math.random() * building.getStoriesNumber()) + 1; 
		return  numberStory;
	}
	
	private void initDispatchStory(){
		int numberDispatchStory = initNumberStory();
		this.dispatchStory = building.getStories()[numberDispatchStory - 1];
	}

	private void initDestinationStory(){
		int numberDestinationStory;
		do {
			numberDestinationStory = initNumberStory();
			
		} while (numberDestinationStory  == dispatchStory.getNumberStory() );
		
		this.destinationStory = building.getStories()[numberDestinationStory-1];
	}
	
	private void initDirection(){   
		this.direction = ((destinationStory.getNumberStory() - dispatchStory.getNumberStory()) > 0) ? Direction.UP : Direction.DOWN;
	}
	
	
	public Direction getDirection(){
		return direction;
	}

	public TransportationState getTransportationState(){
		return transportationState;
	}

	private void setTransportationState(TransportationState transportationState){
		this.transportationState = transportationState;
		gui.getArea().setText(getMessageState());
	}
	
	public boolean isReadyTransport(){
		return isReadyTransport;
	}

	public boolean  requestEnter(Story st){

		boolean isTrueStory = building.getElevator().getCurrentStory().equals(dispatchStory);
		boolean isBoading = building.getElevator().getAction().equals(Actions.BOARDING);
		
		if (gui.isAborted()){
			setTransportationState(TransportationState.ABORTED);
			return true;
		
		} else if (isNotTriedEnter && isTrueStory && isBoading){ 
			boolean isPermitEnter = building.getElevator().getController().permitEnter(st,this);
			return  isPermitEnter;
		
		} else {
			return false;
		}
	}
	
	
	public boolean  requestExit(Story st){
		
		boolean  isDeboating = building.getElevator().getAction().equals(Actions.DEBOARDING);
		
		if (gui.isAborted()) {
			setTransportationState(TransportationState.ABORTED);
			return true;
		
		} else if (isDeboating && isNotTriedExit){
			boolean isPermitExit =  building.getElevator().getController().permitExit(this,st);
			return isPermitExit;
		
		} else {
			return false;
		}
	}

	public void enter(){
		
		threadEnter.start();
		
			while (!threadEnter.getState().equals(Thread.State.TERMINATED)) {
				
				if (gui.isAborted()) {
					setTransportationState(TransportationState.ABORTED);
					break;
				
				} else {
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
					}
				}	
		
			} if (!gui.isAborted()) {
				building.getElevator().getCurrentStory().getDispatchStoryContainer().removePassenger(this);
				building.getElevator().getElevatorContainer().addPassenger(this);     
				logger.info(getMessageAction(getDispatchStory(),transportationState));
			}
	}
	
	public void exit(){
		threadExit.start();
		while (!threadExit.getState().equals(Thread.State.TERMINATED)) {
		
			if (gui.isAborted()) {
				setTransportationState(TransportationState.ABORTED);
				break;
			
			} else {
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
				}
			}
		
		} if (! gui.isAborted()) {	
			building.getElevator().getElevatorContainer().removePassenger(this);
			building.getElevator().getCurrentStory().getArrivalStoryContainer().addPassenger(this);        
			setTransportationState(TransportationState.COMPLETED);
			logger.info(getMessageAction(getDestinationStory(),transportationState));	
		}
	}
	
	public TransportationTask createTransportationTask(){
		setTransportationState(TransportationState.IN_PROGRESS);
		return  new TransportationTask();
	}
	
	
	private String getMessageState(){
		message = new StringBuilder();
		message.append(Constants.PASSENGER + ID);
		message.append(Constants.SPACE);
		message.append(transportationState);
		message.append(Constants.NEW_LINE);
		return message.toString();
	}
	
	private String getMessageAction(Story st, TransportationState state){
		message = new StringBuilder();
		if (state.equals(TransportationState.COMPLETED)) {
			message.append(Constants.DEBOARDING);
		} else {
			message.append(Constants.BOARDING);
		}
		message.append(ID);
		message.append(Constants.ON_STORY);
		message.append(st.getNumberStory());
		message.append(Constants.CLOSE_BRACE);
		return message.toString();
	}
	
	public class TransportationTask implements Runnable {
		
		private TransportationTask() {
			super();
		}
		
		@Override
		public void run(){
				
			synchronized (dispatchStory.getMonitor()) {
				while (!requestEnter(dispatchStory)) {
				
					try {
						setReadyTransport(true);
						dispatchStory.getMonitor().notifyAll();
						dispatchStory.getMonitor().wait();
						
					} catch (InterruptedException e) {
						logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
					}
				}
			
				if (!transportationState.equals(TransportationState.ABORTED)) {
					enter();
					dispatchStory.getMonitor().notifyAll();
				}
			}
			
			if (!transportationState.equals(TransportationState.ABORTED)) {
				synchronized (Actions.DEBOARDING) {
					while (!(requestExit(building.getElevator().getCurrentStory()))) { 	
							
						try {
							
							Actions.DEBOARDING.notifyAll();
							Actions.DEBOARDING.wait();
						
						} catch (InterruptedException e) {
							logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
						}          
					}
				
					if (!transportationState.equals(TransportationState.ABORTED)) {	
						exit();
						Actions.DEBOARDING.notifyAll();
					}
				}	
				
			}	
		}	
	}
}
	

		




































	
				
								
				
		
		
		
			



