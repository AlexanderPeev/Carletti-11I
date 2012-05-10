package gui;

import javax.swing.JPanel;

public class TraysCPanel extends JPanel {

	private MainFrame owner = null;

	public TraysCPanel(MainFrame owner) {
		this.owner = owner;
	}

	public MainFrame getOwner() {
		return this.owner;
	}
}
