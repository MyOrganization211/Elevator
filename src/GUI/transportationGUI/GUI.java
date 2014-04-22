package GUI.transportationGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import beans.Building;
import beans.Constants;

public class GUI extends JFrame implements Runnable {
	
	private final String FILE_PATH            = "elevator.log";
	private final String ERROR_FILE_NOT_FOUND = "Error file elevator.log - not found";
	private final String ERROR                = "Error";
	private final String ERROR_CLOSE_SCANNER  = "Error close";
	private final String TITLE_LOG_FRAME      = "LOG FILE";
	private final String NEW_LINE             = "\r\n";
	private	GUIBuild guiBuild;
	private	Building building;
	private int  storiesNumber;
	private int animationBoost;
	private boolean startState = false;
	private boolean isAborted = false;
	private int imgHeight = 221;
	private int imgWidth = 790; 
	private JFrame baseFrame;
	private JFrame logFrame;
	private JScrollPane jsp ;
	private JPanel buildPanel;
	private JPanel buttonPanel;
	private JPanel textPanel;
	private JTextArea area ;
	private JButton start = new JButton("start");
	private JButton abort = new JButton("abort");
	private JButton view = new JButton("VIEW LOG FILE");
	private  Logger logger = Logger.getLogger(GUI.class);
	
	
	public GUI() throws HeadlessException {
		super();
		
	}
	public GUI( Building building,int animationBoost) throws HeadlessException {
		super();
		this.animationBoost= animationBoost;
		this.building = building;
		if (animationBoost==0) {
			setStartState(true);
		}
		this.storiesNumber = this.building.getStoriesNumber();
	}
	
	public int getNumberStories() {
		return storiesNumber;
	}
	
	public void setNumberStories(int numberStories) {
		this.storiesNumber = numberStories;
	}
	
	public Building getBuilding() {
		return building;
	}
	
	public JTextArea getArea() {
		return area;
	}
	
	void setStartState(boolean state) {
		startState = state;
	}
	
	public boolean getStartState(){
		return startState ;
	}
	
	public GUIBuild getGuiBuild() {
		return guiBuild;
	}

	public void setGuiBuild(GUIBuild guiBuild) {
		this.guiBuild = guiBuild;
	}
	
	public boolean isAborted() {
		return isAborted;
	}
	public JButton getAbort() {
		return abort;
	}
	public void setAbort(JButton abort) {
		this.abort = abort;
	}

	public JButton getView() {
		return view;
	}
	public void setView(JButton view) {
		this.view = view;
	}
	
	@Override
	public void run() {
	
		guiBuild = new GUIBuild(building,animationBoost);
		guiBuild.setPreferredSize(new Dimension(imgWidth,imgHeight * (storiesNumber)));
		
		Thread thread = new Thread(guiBuild);
		
		baseFrame = new JFrame("Elevator");
		BorderLayout layout = new BorderLayout();
		
		baseFrame.setLayout(layout);
		baseFrame.setBounds(300, 50, imgWidth, imgHeight * 4);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		buildPanel = new JPanel();
		buildPanel.add(guiBuild);
		
		buttonPanel =  new JPanel(); 
		buttonPanel.setPreferredSize(new Dimension(835,50));
		
		getAbort().setVisible(false);
		getView().setVisible(false);		
		
		buttonPanel.add(start);
		buttonPanel.add(getAbort());
		buttonPanel.add(getView());
		
		textPanel = new JPanel();
		textPanel.setPreferredSize(new Dimension(835,50));
		
		area = new JTextArea(4,1);
		area.setPreferredSize(new Dimension(750,50));
		area.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		area.setEditable(false);
		textPanel.add(area);
		
		jsp = new JScrollPane(buildPanel);
		jsp.setPreferredSize(new Dimension(imgWidth, imgHeight * 3 + 25));
		jsp.setVisible(true);
		
		baseFrame.add(buttonPanel, BorderLayout.SOUTH);
		baseFrame.add(textPanel, BorderLayout.CENTER);
		baseFrame.add(jsp, BorderLayout.NORTH);			
				
		thread.start();
		
		if (animationBoost > 0) {
			baseFrame.setVisible(true);
		}
		
		while (!thread.getState().equals(Thread.State.TERMINATED)) {
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error(Thread.currentThread().getName() + Constants.THREAD_ERROR + e.getMessage());
			}
		}
		
		start.addActionListener(new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent e) {
				
				start.setVisible(false);
				getAbort().setVisible(true);
				guiBuild.getTimer().start();
				setStartState(true);
			}
		});
		
		
		getAbort().addActionListener( new ActionListener() {
			
			@Override
			 public void actionPerformed(ActionEvent e) {
				
				getAbort().setVisible(false);
				getView().setVisible(true);
				guiBuild.getTimer().stop();
				isAborted = true;
			}
		});
		
		getView().addActionListener( new ActionListener() {
			
			@Override
			 public void actionPerformed(ActionEvent e) {
				
				logFrame = new JFrame(TITLE_LOG_FRAME);
				JTextArea jta;
			    JScrollPane jsp ;
			    Scanner sc = null;
			    StringBuffer sb = new StringBuffer();
				try {
					
					sc = new Scanner(new FileReader(FILE_PATH));
					
					while (sc.hasNextLine()) {
						 sb.append(sc.nextLine());
						 sb.append(NEW_LINE);
					}
					
				} catch (FileNotFoundException exc) { 
					
					JFrame errorFarame = new JFrame();
                	JOptionPane.showMessageDialog(errorFarame, ERROR_FILE_NOT_FOUND,
                							      ERROR, JOptionPane.ERROR_MESSAGE);
                	errorFarame.setVisible(true);
				}  
	
				if (sc != null) {
					try {
						sc.close();
					} catch (IllegalStateException exp) {
						logger.error(ERROR_CLOSE_SCANNER + exp.getMessage());
					}
				}
				
			    jta = new JTextArea(sb.toString());
				jsp = new JScrollPane(jta);
				 
				logFrame.add(jsp);
				logFrame.setBounds(300, 50, 500, 800);
				logFrame.setVisible(true);
			
			}
		});
	}
	
}
 