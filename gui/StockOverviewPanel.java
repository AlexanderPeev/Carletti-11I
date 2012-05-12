package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.Stock;

/**
 * A panel for displaying the contents of a Stock object. It provides a visual
 * representation of all storage units, and means to select and pick trays.
 * 
 * @author Alexander Peev
 * 
 */
public class StockOverviewPanel extends JPanel {

	private DashboardPanel owner = null;
	private JPanel pnlStorageUnits;
	private Stock stock;

	public StockOverviewPanel(DashboardPanel owner) {
		this.owner = owner;
		setLayout(new BorderLayout(0, 0));

		JPanel pnlEast = new JPanel();
		add(pnlEast, BorderLayout.EAST);
		pnlEast.setLayout(new BoxLayout(pnlEast, BoxLayout.Y_AXIS));

		pnlStorageUnits = new JPanel();
		pnlStorageUnits.setLayout(new GridLayout(1, 0, 0, 0));

		JScrollPane scpMain = new JScrollPane(pnlStorageUnits);
		add(scpMain, BorderLayout.CENTER);
	}

	public Stock getStock() {
		return this.stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public DashboardPanel getOwner() {
		return this.owner;
	}
}
