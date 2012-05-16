/**
 * @author Ricardas Risys
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Stock;
import model.User;
import service.Service;

public class DashboardPanel extends JPanel {

	private MainFrame owner = null;
	private JButton btnCreateStock, btnUpdateStock, btnDeleteStock;
	private JTextArea txaStockInfo;
	private JScrollPane scpStockInfo;
	private SelectStockListener selectStockLisneter = new SelectStockListener();
	private SquareGraphic selectedSquare = null;
	private StockOverviewPanel stockOverviewPanel = new StockOverviewPanel(this);

	public DashboardPanel(MainFrame owner) {
		User user = Service.getCurrentUser();

		this.owner = owner;
		this.setLayout(new BorderLayout());

		JPanel pnlDashboardCenter = new JPanel();
		pnlDashboardCenter.setLayout(new FlowLayout(FlowLayout.LEFT));
		JScrollPane scroll = new JScrollPane(pnlDashboardCenter);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scroll, BorderLayout.CENTER);

		JPanel pnlDashboardRight = new JPanel();
		this.add(pnlDashboardRight, BorderLayout.EAST);
		pnlDashboardRight.setLayout(new BoxLayout(pnlDashboardRight,
				BoxLayout.Y_AXIS));

		SquareGraphic sq;
		Set<Stock> stocks = Service.getAllStocks();
		for (Stock stock : stocks) {
			sq = new SquareGraphic();
			sq.addMouseListener(selectStockLisneter);
			sq.setPreferredSize(new Dimension(85, 85));
			sq.setStock(stock);
			pnlDashboardCenter.add(sq);
		}

		// finish later when service will be working
		pnlDashboardCenter.setPreferredSize(new Dimension(400, 24 / 4 * 90));

		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(3, 1));
		pnlDashboardRight.add(pnlButtons);

		if (user.canAccess("Create Stock")) {
			btnCreateStock = new JButton("Create Stock");
			pnlButtons.add(btnCreateStock);
		}

		if (user.canAccess("Update Stock")) {
			btnUpdateStock = new JButton("Update Stock");
			pnlButtons.add(btnUpdateStock);
			btnUpdateStock.setEnabled(false);
		}

		if (user.canAccess("Delete Stock")) {
			btnDeleteStock = new JButton("Delete Stock");
			pnlButtons.add(btnDeleteStock);
			btnDeleteStock.setEnabled(false);
		}

		txaStockInfo = new JTextArea("No Stock selected");
		txaStockInfo.setEnabled(false);
		txaStockInfo.setLineWrap(true);
		txaStockInfo.setBackground(Color.LIGHT_GRAY);

		scpStockInfo = new JScrollPane(txaStockInfo);
		scpStockInfo.setPreferredSize(new Dimension(210, 300));
		pnlDashboardRight.add(scpStockInfo);

	}

	public MainFrame getOwner() {
		return this.owner;
	}

	class SelectStockListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getClickCount() == 2) {
				if (selectedSquare != null) stockOverviewPanel
						.setStock(selectedSquare.getStock());
				getOwner().navigateTo(stockOverviewPanel);
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// not used
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// not used
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// not used
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			SquareGraphic square = (SquareGraphic) arg0.getSource();

			if (selectedSquare != null && !square.equals(selectedSquare)) {
				selectedSquare.deselect();
			}

			square.toggleClick();
			selectedSquare = square;

			String output = "";
			if (square.getIsSelected()) {
				Stock stock = square.getStock();
				output = "Selected stock:\n" + stock.getName();
				output += "\n\nStock Type:\n" + stock.getType();
				output += "\n\nCapacity:\n" + stock.getCapacity()
						+ " storage units";
				output += "\n\nMax Trays per Storage Unit:\n"
						+ stock.getMaxTraysPerStorageUnit();

				btnUpdateStock.setEnabled(true);
				btnDeleteStock.setEnabled(true);
			}
			else {
				output = "No Stock selected";
				btnUpdateStock.setEnabled(false);
				btnDeleteStock.setEnabled(false);
			}

			txaStockInfo.setText(output);
		}
	}
}
