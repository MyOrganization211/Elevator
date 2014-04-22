package beans;

import org.apache.log4j.Logger;

import Enums.Actions;
import Enums.Direction;
import Enums.TransportationState;
import GUI.transportationGUI.GUI;

public 	class Elevator {

	private int elevatorCapacity;
	private Story currentStory;   
	private Actions action;   
	private Controller controller = new Controller(); 
	private PassengersList elevatorContainer = new PassengersList(); 
	private Direction direction = Direction.UP;
	
	
	
	public Elevator(int elevatorCapacity){
		this.elevatorCapacity = elevatorCapacity;
		this.action = Actions.NOT_STARTING_TRANSPORTATION;
	}
	
	public int getElevatorCapacity(){
		return elevatorCapacity;
	}

	
	public PassengersList getElevatorContainer(){
		return elevatorContainer;
	}

	public void setElevatorContainer(PassengersList elevatorContainer){
		this.elevatorContainer = elevatorContainer;
	}

	public void setElevatorCapacity(int elevatorCapacity){
		this.elevatorCapacity = elevatorCapacity;
	}


	public Direction getDirection(){
		return direction;
	}

	public void setDirection(Direction direction){
		this.direction = direction;
	}
	
	public Story getCurrentStory(){
		return currentStory;
	}

	public void setCurrentStory(Story currentStory){
		this.currentStory = currentStory;
	}

	public Actions getAction(){
		return action;
	}

	public void setAction(Actions action){
		this.action = action;
	}

	public Controller getController(){
		return controller;
	}


	public void setController(Controller controller){
		this.controller = controller;
	}

	public  class Controller {
		
		private int countPermitExit;
		private int countPermitEnter;
		private GUI gui;
		private Logger logger = Logger.getLogger(Controller.class);
		private StringBuilder message;
		
		private Controller() {  
			super();
		}
			
		public GUI getGui() {
			return gui;
		}

		public void setGui(GUI gui) {
			this.gui = gui;
		}

		private void deboard(Story st,PassengersList allPassengers) {
			countPermitExit = 0;
			int currentElevatorContainerSize = elevatorContainer.size();
			
			for(Passenger passenger : elevatorContainer.getContainer()) {
				passenger.setNotTriedExit(true);
			}
			
			synchronized (Actions.DEBOARDING) {
				
				setAction(Actions.DEBOARDING);
				Actions.DEBOARDING.notifyAll();
				
				while (currentElevatorContainerSize > countPermitExit) {              
					try {
						
						if (gui.isAborted()) {
							 notifyAllPassengers(allPassengers);	
							break;
						}
						
						Actions.DEBOARDING.wait();
					} catch (InterruptedException e) {
						logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
					}
				}	
			}
		}
		
		private void board(Story st,PassengersList allPassengers) {
			countPermitEnter = 0;
			int currentDispatchSize = st.getDispatchStoryContainer().size();
			
			for(Passenger passenger : st.getDispatchStoryContainer().getContainer()) {
		    	 passenger.setNotTriedEnter(true);
		     }
			                                                                                                                               
			setAction(Actions.BOARDING);
			st.getMonitor().notifyAll();
							
			while (currentDispatchSize > countPermitEnter) {
				try {
					if (gui.isAborted()){
						 notifyAllPassengers(allPassengers);	
						break;
					}
					
					st.getMonitor().wait();
				
				} catch (InterruptedException e) {
					logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
				}
			}
		}
			
		public boolean permitExit(Passenger passenger,Story st) {
			boolean isPermitExit = (currentStory.getNumberStory() == passenger.getDestinationStory().getNumberStory());
			countPermitExit++;
			passenger.setNotTriedExit(false);
			return isPermitExit;
		}
		
		public boolean permitEnter(Story st,Passenger passenger) {
			boolean isNotFullElevator = (elevatorContainer.size() < elevatorCapacity);
			boolean isTrueDirection = passenger.getDirection().equals(direction);
			countPermitEnter++;
			passenger.setNotTriedEnter(false);
			return (isNotFullElevator && isTrueDirection);
		}
				
		private boolean isNotValidateNumberPassengers(Story [] stories,PassengersList allPassengers) {
			boolean isNotValidate = false;
			int numberArrivalPassengers = 0;
		
			for(Story story : stories){
				numberArrivalPassengers += story.getArrivalStoryContainer().size();
			}
			isNotValidate = (numberArrivalPassengers != allPassengers.size());
			
			return isNotValidate;
		}
	
		private  boolean isNotEmptyDispatchStoryContainers(Story [] stories) {
			boolean isNotEmpty = false;
			
			for ( Story st:  stories){
				if( !st.getDispatchStoryContainer().isEmpty()){
					isNotEmpty = true;
				}
			}		
			return isNotEmpty; 
		}

		private boolean isNotValidateStateArrivalPassengers(Story [] stories) {
			boolean isNotValidateState = false; 
			
			for (Story story : stories){
				for(Passenger passenger : story.getArrivalStoryContainer().getContainer()) {
					if(	! passenger.getTransportationState().equals(TransportationState.COMPLETED)) {
						isNotValidateState = true;
					}
				}
			}
			return isNotValidateState; 
		}
		
		private boolean isNotValidateStoryArrivalPasssengers(Story [] stories) { 
			boolean isNotValidateNumber = false;
		
			for (Story story : stories){
				for (Passenger passenger : story.getArrivalStoryContainer().getContainer()) {
					if (passenger.getDestinationStory().getNumberStory() != story.getNumberStory()) { 
						isNotValidateNumber = true;
					}
				}
			}
			return isNotValidateNumber;
		}
		
		private boolean isValidateTransportation(Story [] stories ,PassengersList allPassengers) {
			boolean isValidateTransportation = false;
			
			if (action.equals(Actions.ABORTING_TRANSPORTATION)) {
				return true;
			}
			
			else if (action.equals(Actions.COMPLETION_TRANSPORTATION)) {
					if (!isNotEmptyDispatchStoryContainers(stories)) {
						logger.info(Constants.CHECK_DISPATCH_TRUE);
					} else {
						logger.info(Constants.CHECK_DISPATCH_FALSE);
					}
							 
					if (elevatorContainer.isEmpty()) {
						logger.info(Constants.CHECK_ELEVATOR_TRUE);
					} else {
						logger.info(Constants.CHECK_ELEVATOR_FALSE);
					}
					
					if (!isNotValidateStateArrivalPassengers(stories)) {
						logger.info(Constants.CHECK_STATE_TRUE);
					} else {
						logger.info(Constants.CHECK_STATE_FALSE);
					}
										
					if (!isNotValidateStoryArrivalPasssengers(stories)) {
						logger.info(Constants.CHECK_NUMBER_STORY_TRUE);
					} else {
						logger.info(Constants.CHECK_NUMBER_STORY_FALSE);
					}
					
					if (!isNotValidateNumberPassengers(stories, allPassengers)) {
						logger.info(Constants.CHECK_NUMBER_PASSENGERS_TRUE);
					} else {
						logger.info(Constants.CHECK_NUMBER_PASSENGERS_FALSE);
					}
				}
											
				isValidateTransportation = ((!isNotEmptyDispatchStoryContainers(stories)) &&
						elevatorContainer.isEmpty() && (!isNotValidateStateArrivalPassengers(stories)) &&
						(!isNotValidateStoryArrivalPasssengers(stories)) &&
						(!isNotValidateNumberPassengers(stories, allPassengers)));
				
				return isValidateTransportation; 
			}
		
		
		private void notifyAllPassengers(PassengersList allPassengers) {
			setAction(Actions.ABORTING_TRANSPORTATION);
			for (Passenger passenger: allPassengers.getContainer()) {
				
				synchronized (passenger.getDispatchStory().getMonitor()) {
					passenger.getDispatchStory().getMonitor().notifyAll();
				}
				synchronized (Actions.DEBOARDING) {
					Actions.DEBOARDING.notifyAll();
				}
			}
		}	
	
		private boolean isAllReadyTransport(PassengersList allPassengers) {
			boolean isAllReadyTransport = true;
			
			for (Passenger passenger: allPassengers.getContainer()) {
				if (!passenger.isReadyTransport()) {
					isAllReadyTransport = false;
				}
				
			}
			return isAllReadyTransport;
		}
			
		public void transport(Story [] stories,PassengersList allPassengers) {
			int fromStory = 0;
			int toStory = 0;
			
			setAction(Actions.STARTING_TRANSPORTATION);
			logger.info(Actions.STARTING_TRANSPORTATION);
			while (!isAllReadyTransport(allPassengers)) {
		   		
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
				}
			}
		
			while (!isValidateTransportation(stories,allPassengers)) {
				setDirection(Direction.UP);	
				for (int i = 0 ;i < stories.length - 1 ;i++ ) {
					fromStory = i + 1;	
					toStory = i + 2;
					
					if (gui.isAborted()) {
						 notifyAllPassengers(allPassengers);
						 break;
					 }	
					 setCurrentStory(stories[i]);
					
					 synchronized (getCurrentStory().getMonitor()) {
						 deboard(getCurrentStory(), allPassengers);
						
						 if (gui.isAborted()) {
							 notifyAllPassengers(allPassengers);	
							 break;
						 }
						 
						 board(getCurrentStory(), allPassengers);
						
						 if (gui.isAborted()) {
							 notifyAllPassengers(allPassengers);	
							 break;
						 }
						
						 setAction(Actions.MOVING_ELEVATOR);	
						 logger.info(getMessage(fromStory,toStory));
					 }  
				}
				if (action.equals(Actions.ABORTING_TRANSPORTATION)) {
					break;
				}		 
				setDirection(Direction.DOWN);
				for (int i = stories.length -1;i >= 0 ;i--) {
					fromStory = i + 1;	
					toStory = i;
					
					if (gui.isAborted()){
						notifyAllPassengers(allPassengers);	
						break;
					}
					setCurrentStory(stories[i]);
					synchronized (getCurrentStory().getMonitor()) {
						
						deboard(getCurrentStory(),allPassengers);
						
						if (gui.isAborted()) {
							 notifyAllPassengers(allPassengers);	
							 break;
							}
						
						board(getCurrentStory(),allPassengers);
						
						if (gui.isAborted()) {
							 notifyAllPassengers(allPassengers);	
							 break;
							}
						if (i != 0) {
							logger.info(getMessage(fromStory, toStory));
							setAction(Actions.MOVING_ELEVATOR);	
						}
					 }
				}
			
			} if (!action.equals(Actions.ABORTING_TRANSPORTATION)) {
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
				}
		
				setAction(Actions.COMPLETION_TRANSPORTATION); 		
				gui.getArea().setText(Actions.COMPLETION_TRANSPORTATION + Constants.NEW_LINE);
				logger.info(Actions.COMPLETION_TRANSPORTATION);
				isValidateTransportation(stories,allPassengers);
				gui.getAbort().setVisible(false);
				gui.getView().setVisible(true);
			
			} else {
				gui.getArea().setText(Actions.ABORTING_TRANSPORTATION + Constants.NEW_LINE);
				logger.info("ABORTING_TRANSPORTATION");
			}
			
			gui.getGuiBuild().getTimer().stop(); 
			
			}
	   
     
		private String getMessage(int fromStory, int toStory) {
			message = new StringBuilder();
			message.append(Constants.MOVING_FROM_STORY);
			message.append(fromStory);
			message.append(Constants.TO_STORY);
			message.append(toStory);
			message.append(Constants.CLOSE_BRACE);
			return message.toString();
		}
	
	}

}
