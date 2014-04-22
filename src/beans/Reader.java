package beans;

import java.util.ResourceBundle;


public class Reader {
	 
	private String fileName;

	public Reader(String fileName) { 
		super();
		this.fileName = fileName;
	}

	private ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle(fileName);
	}
	
	
	public int getValue(String key) {
		return Integer.valueOf(getResourceBundle().getString(key));
	}
}
	
	
	

