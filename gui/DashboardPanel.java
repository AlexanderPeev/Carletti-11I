package gui;

import javax.swing.JPanel;

public class DashboardPanel extends JPanel {

	private MainFrame owner = null;

	public DashboardPanel(MainFrame owner) {
		this.owner = owner;
	}

	public MainFrame getOwner() {
		return this.owner;
	}
}
