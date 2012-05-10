package gui;

import javax.swing.JPanel;

public class StatisticsPercentageOfWastePanel extends JPanel {

	private MainFrame owner = null;

	public StatisticsPercentageOfWastePanel(MainFrame owner) {
		this.owner = owner;
	}

	public MainFrame getOwner() {
		return this.owner;
	}
}
