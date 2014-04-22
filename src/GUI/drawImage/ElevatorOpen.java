package GUI.drawImage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ElevatorOpen extends JPanel {
	
	public Image  elevatorOpen = new ImageIcon(getClass().getClassLoader().getResource("images/open.png")).getImage();
	
	public void paint (Graphics g) {
		g = (Graphics2D)g;
		g.drawImage(elevatorOpen, 0, 0, null);
	}
	
}
