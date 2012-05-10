package gui;

import javax.swing.JPanel;

public class StatisticsAveragePickingTimesPanel extends JPanel {

	private MainFrame owner = null;

	public StatisticsAveragePickingTimesPanel(MainFrame owner) {
		this.owner = owner;
	}

	public MainFrame getOwner() {
		return this.owner;
	}
}
