package beans;

public class Building {

	private int storiesNumber;
	private Elevator elevator;
	private Story[] stories;

	public Building(int storiesNumber, Elevator elevator) {
		
		this.storiesNumber = storiesNumber; 
		this.elevator = elevator;
		initStories();
		initElevator();
	}

	public Story[] getStories() {
		return stories;
	}
	
	public void setStories(Story[] stories) {
		this.stories = stories;
	}

	public int getStoriesNumber() {
		return storiesNumber;
	}
	
	public void setStoriesNumber(int storiesNumber) {
		this.storiesNumber = storiesNumber;
	}

	public Elevator getElevator() {
		return elevator;
	}

	public void setElevator(Elevator elevator) {
		this.elevator = elevator;
	}

	private void initElevator() {
		this.elevator.setCurrentStory(stories[0]);
	}

	private void initStories() {
		this.stories = new Story[storiesNumber];
		for (int i = 0; i < storiesNumber; i++) {
			stories[i] = new Story();
		}
	}
}
			




