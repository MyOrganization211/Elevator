package beans;



public class Story {
	
	private static int numberOfStories = 1;
	private int numberStory;
	private PassengersList dispatchStoryContainer = new PassengersList();
	private PassengersList arrivalStoryContainer = new PassengersList();
	private volatile  Object monitor = new Object(); 
	
	
	public Story() {
		this.numberStory = numberOfStories++;
	}
	
	public int getNumberStory() {
		return numberStory;
	}
		
	public PassengersList getDispatchStoryContainer() {
		return dispatchStoryContainer;
	}

	public void setDispatchStoryContainer(PassengersList dispatchStoryContainer) {
		this.dispatchStoryContainer = dispatchStoryContainer;
	}

	public PassengersList getArrivalStoryContainer() {
		return arrivalStoryContainer;
	}

	public void setArrivalStoryContainer(PassengersList arrivalStoryContainer) {
		this.arrivalStoryContainer = arrivalStoryContainer;
	}

	public Object getMonitor() {
		return monitor;
	}
	
}


