package GUI.transportationGUI;

import org.apache.log4j.Logger;

import beans.Constants;


public class MoveExit implements Runnable {
	
	private GUI gui;
	private GUIBuild guiBuild ;
	private Object sync;
	private int ofset = 76;
	private Logger logger = Logger.getLogger(MoveExit.class);	
	
	public MoveExit(GUI gui) {
		this.gui = gui;
		this.guiBuild = gui.getGuiBuild();
		this.sync = guiBuild.getSync();
	}
	
	
	@Override
	 public void run() {
		synchronized (sync) {
			if (!gui.isAborted()) {
				
				if (guiBuild.getSizeElevatorContainer() > 0) {	
					
					do {
						guiBuild.setMoved(true);
						guiBuild.xPositionExit -= ofset;
						
						try {
							sync.wait();
						} catch (InterruptedException e) {
							logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
						}
				
						} while (guiBuild.xPositionExit >= 152 + ( 76 * guiBuild.getSizeArrivalContainer()));
				}
			
			} else {
				guiBuild.getTimer().stop();
			}
			
			if ((guiBuild.xPositionExit >= 76) | (guiBuild.getSizeElevatorContainer() == 0)) {
				
				guiBuild.xPositionExit = 684;
				guiBuild.setMoved(false);
			}
		}
	  }
	
}