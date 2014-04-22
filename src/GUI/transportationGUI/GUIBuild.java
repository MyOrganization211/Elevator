package GUI.transportationGUI;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import GUI.drawImage.ElevatorClose;
import GUI.drawImage.ElevatorOpen;
import GUI.drawImage.PassengerEnter;
import GUI.drawImage.PassengerExit;
import beans.Building;


public class GUIBuild 	 extends JPanel implements ActionListener, Runnable {
	
	private int numberStories;
	private int currentStory = 0; 
	private volatile int sizeArrivalContainer ;
	private volatile int sizeDispatchContainer;
	private volatile int sizeElevatorContainer;
	private volatile int arrivalContainers [];
	private volatile int dispatchContainers [];
	private int animationSpeed = 500;
	private boolean isInit = true;
	private boolean isMoved = false;
	private Timer timer;
	private ElevatorClose elevatorClose   = new ElevatorClose();
	private ElevatorOpen elevatorOpen     = new ElevatorOpen();
	private PassengerEnter passengerEnter = new PassengerEnter();
	private PassengerExit passengerExit   = new PassengerExit();
	private Object sync = new Object();
	private Building building;
	
	 int xPositionEnter;
	 int xPositionExit= 684;
	private int yPositionOffsetEnter;
	private int yPositionOffsetExit;
	
	public GUIBuild() {
		super();
		
	}
		
	public GUIBuild(Building building,int animationBoost) {
		
		this.building = building;
		numberStories = building.getStoriesNumber();
	
		if (animationBoost > 0) {				
			animationSpeed /= animationBoost;
		}

		arrivalContainers = new int[numberStories];
		dispatchContainers = new int[numberStories];
	}
		
	
	public Object getSync() {
		return sync;
	}


	public boolean isMoved() {
		return isMoved;
	}

	
	public void setMoved(boolean isMoved) {
		this.isMoved = isMoved;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	public int getSizeArrivalContainer() {
		return sizeArrivalContainer;
	}

	public int getSizeDispatchContainer() {
		return sizeDispatchContainer;
	}

	public int getSizeElevatorContainer() {
		return sizeElevatorContainer;
	}

	public Timer getTimer() {
			return timer;
	}
	
	@Override
	public void run() {	
		timer = new Timer(animationSpeed, this);
	}
	
	public void paint (Graphics g){
	
		for (int i = 0 ; i < numberStories; i++) {
			arrivalContainers [i] = building.getStories()[i].getArrivalStoryContainer().size();
			dispatchContainers [i] = building.getStories()[i].getDispatchStoryContainer().size();;
			sizeElevatorContainer = building.getElevator().getElevatorContainer().size();
			
		}
		
		for (int i = 0 ; i< numberStories;i++) {
			
			sizeArrivalContainer = arrivalContainers[(i - numberStories) * (-1) - 1];
	
			sizeDispatchContainer = dispatchContainers[(i - numberStories) * (-1) - 1];
			
			
			if (i > 0){
				yPositionOffsetEnter = 145; yPositionOffsetExit = 195;
			}
				
			if ((((i-numberStories)*(-1)) == currentStory) & (!isInit)) {
				g.drawImage(elevatorOpen.elevatorOpen, 0, i * 221, null);
				
				if (isMoved) {
					g.drawImage(passengerExit.passenger, xPositionExit, (i + 1) * 30 + yPositionOffsetExit * i, null);
				}
	
				if (sizeArrivalContainer >= 1) { 
					for (int x=1 ; x <= sizeArrivalContainer; x++) {
					g.drawImage(passengerExit.passenger, (76 * x ), (i+1)*30 + yPositionOffsetExit * i, null);
					}
				}
				
				if (sizeDispatchContainer > 0) { 
					g.drawImage(passengerEnter.passenger, xPositionEnter, (i + 1) * 75 + yPositionOffsetEnter * i, null);
					
					if (sizeDispatchContainer > 1) {
						
						for (int x=2; x <= sizeDispatchContainer; x++){
							g.drawImage(passengerEnter.passenger, (76 * x), (i + 1) * 75 + yPositionOffsetEnter * i, null);
						}
					}
				}
				
			} else { 
			
				g.drawImage(elevatorClose.elevatorClose, 0, i * 221, null);
				
				if (sizeArrivalContainer > 0) {
					for (int x=1; x <= sizeArrivalContainer; x++){
						g.drawImage(passengerExit.passenger, (76 * x), (i + 1) * 30 + yPositionOffsetExit * i, null);
						
					}
				}
				
				if (sizeDispatchContainer > 0) { 
					for(int x=1;x<=sizeDispatchContainer;x++){
						g.drawImage(passengerEnter.passenger, (76 * x), (i + 1) * 75 + yPositionOffsetEnter * i, null);
					}
				}	
			}
		}
			
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		isInit=false;
		
		synchronized (sync) {
			currentStory = building.getElevator().getCurrentStory().getNumberStory();
			repaint();
			sync.notify();
		}
	}
}


