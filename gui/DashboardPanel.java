/**
 * @author Ricardas Risys
 */

package gui;

import gui.SquareGraphic.StockState;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Stock;
import model.StockType;
import model.User;
import service.Service;

public class DashboardPanel extends JPanel {

	private MainFrame owner = null;
	private JButton btnCreateStock, btnUpdateStock, btnDeleteStock;
	private JTextArea txaStockInfo;
	private JScrollPane scpStockInfo;
	private SelectStockListener selectStockLisneter = new SelectStockListener();
	private SquareGraphic selectedSquare = null;

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
		Random ran = new Random();
		Stock stock;
		for (int i = 0; i < 21; i++) {
			stock = new Stock("Warehouse Nr. " + i, StockType.MACHINE,
					ran.nextInt(100) + 5, 12);
			sq = new SquareGraphic();
			sq.addMouseListener(selectStockLisneter);
			sq.setPreferredSize(new Dimension(85, 85));
			sq.setStock(stock);
			if (i % 1 == 1) {
				sq.setState(StockState.EARLY);
			}
			if (i % 2 == 1) {
				sq.setState(StockState.MINOPT);
			}
			if (i % 3 == 1) {
				sq.setState(StockState.OPTMAX);
			}
			if (i % 4 == 1) {
				sq.setState(StockState.WASTE);
			}
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
				// go in
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
			} else {
				output = "No Stock selected";
				btnUpdateStock.setEnabled(false);
				btnDeleteStock.setEnabled(false);
			}

			txaStockInfo.setText(output);
		}
	}
}
