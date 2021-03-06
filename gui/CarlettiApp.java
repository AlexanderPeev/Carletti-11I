package gui;

import javax.swing.UIManager;

/**
 * The class which supplies the main method for executing the application with a
 * GUI.
 * 
 * @author Alexander Peev
 * 
 */
public class CarlettiApp {

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			System.out.println("Error setting look & feel: " + e.getMessage());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoginFrame frame = new LoginFrame();
		frame.setVisible(true);
	}

}
