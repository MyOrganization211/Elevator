package GUI.drawImage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ElevatorClose extends JPanel {
	
	public Image  elevatorClose = new ImageIcon(getClass().getClassLoader().getResource("images/close.png")).getImage();
			
	
	public void paint (Graphics g) {
		g = (Graphics2D)g;
		g.drawImage(elevatorClose, 0, 0, null);
	}
	
}
