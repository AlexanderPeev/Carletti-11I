package gui;

import javax.swing.JPanel;

public class NotificationsPanel extends JPanel {

	private MainFrame owner = null;

	public NotificationsPanel(MainFrame owner) {
		this.owner = owner;
	}

	public MainFrame getOwner() {
		return this.owner;
	}
}
