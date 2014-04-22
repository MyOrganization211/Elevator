package GUI.transportationGUI;

import org.apache.log4j.Logger;

import beans.Constants;


public class MoveEnter implements Runnable {

	private GUI gui;
	private	GUIBuild guiBuild ;
	private	Object sync;
	private	int offset = 76; 
	private Logger logger = Logger.getLogger(MoveEnter.class);	
	
	public MoveEnter (GUI gui) {
		this.gui = gui;
		this.guiBuild = gui.getGuiBuild();
		this.sync = gui.getGuiBuild().getSync();
	}
		
		
	@Override
	public void run() {
		synchronized (sync) {
			if (!gui.isAborted()) {
				
				do {
					guiBuild.xPositionEnter += offset;
					
					try {
						sync.wait();
					} catch (InterruptedException e) {
						logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR +e.getMessage());
					}
				
				} while (guiBuild.xPositionEnter <= 600);
			
			} else {
				guiBuild.getTimer().stop();
			}
			
			guiBuild.xPositionEnter=76; 
		}
	}
}
	
