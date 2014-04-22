package GUI.drawImage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PassengerExit extends JPanel {
	
	public Image  passenger = new ImageIcon(getClass().getClassLoader().getResource("images/passengerOut.png")).getImage();
	
	public void paint (Graphics g) {
		g = (Graphics2D)g;
		g.drawImage(passenger, 0, 0, null);
	}
	
}


