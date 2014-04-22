package beans;

import java.util.LinkedList;
import java.util.List;

public class PassengersList {

	private List<Passenger> container;
	
	public PassengersList() {
		container = new LinkedList<Passenger>();
	}
		
	public List<Passenger> getContainer() {
		return container;
	}

	public void setContainer(List<Passenger> container) {
		this.container = container;
	}

	public void addPassenger(Passenger passenger) {
		container.add(passenger);
	}
	
	public void removePassenger(Passenger passenger) {
		container.remove(passenger);
	}

	public int size() {
		return container.size();
	}
  
	public boolean isEmpty() {
	   return container.isEmpty();
   }
}
