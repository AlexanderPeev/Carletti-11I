package gui;

import javax.swing.JPanel;

public class ProductsPanel extends JPanel {

	private MainFrame owner = null;

	public ProductsPanel(MainFrame owner) {
		this.owner = owner;
	}

	public MainFrame getOwner() {
		return this.owner;
	}
}
