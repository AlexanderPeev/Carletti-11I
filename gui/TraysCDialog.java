package gui;

import javax.swing.JDialog;

public class TraysCDialog extends JDialog {

	private MainFrame main = null;

	public TraysCDialog(MainFrame main) {
		this.main = main;
		this.setModal(true);
		this.setTitle("Create Trays");
		this.setResizable(false);
		this.setSize(600, 500);
		this.setLocationRelativeTo(this.getRootPane());
	}

	public MainFrame getMain() {
		return this.main;
	}
}
