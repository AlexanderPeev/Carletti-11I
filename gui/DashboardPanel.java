/**
 * A panel for displaying the contents of all Stock objects. It provides a visual
 * representation of all stock units.
 * 
 * @author Ricardas Risys
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Stock;
import model.User;
import service.Service;
import service.StockNotEmptyException;

public class DashboardPanel extends JPanel implements UpdateObserver {

	private MainFrame owner = null;
	private JPanel pnlDashboardCenter, pnlDashboardRight, pnlButtons;
	private JButton btnCreateStock, btnUpdateStock, btnDeleteStock,
			btnOpenStock;
	private JTextArea txaStockInfo;
	private JScrollPane scpStockInfo;

	private SquareGraphic selectedSquare = null;
	private StockOverviewPanel stockOverviewPanel = new StockOverviewPanel(this);
	private ArrayList<SquareGraphic> squareGraphicList = new ArrayList<SquareGraphic>();
	private List<Stock> stocks = new ArrayList<Stock>();
	private Updater repainter;
	private Controller controller = new Controller();
	private SelectStockListener selectStockLisneter = new SelectStockListener();
	private StockCUDialog dlgStock = new StockCUDialog();

	public DashboardPanel(MainFrame owner) {
		User user = Service.getCurrentUser();

		this.owner = owner;
		this.repainter = Updater.getInstance();// new Updater(60000);
		this.repainter.registerObserver(this);

		this.setLayout(new BorderLayout());

		pnlDashboardCenter = new JPanel();
		pnlDashboardCenter.setLayout(new GridLayout(0, 2, 5, 5));
		JScrollPane scpDashboardCenter = new JScrollPane(pnlDashboardCenter);
		scpDashboardCenter
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scpDashboardCenter, BorderLayout.CENTER);

		fillStocks();

		pnlDashboardRight = new JPanel();
		this.add(pnlDashboardRight, BorderLayout.EAST);
		pnlDashboardRight.setLayout(new BoxLayout(pnlDashboardRight,
				BoxLayout.Y_AXIS));

		pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(0, 1));
		pnlDashboardRight.add(pnlButtons);
		btnCreateStock = new JButton("Create Stock");
		btnUpdateStock = new JButton("Update Stock");
		btnDeleteStock = new JButton("Delete Stock");
		btnOpenStock = new JButton("Open Stock");

		if (user.canAccess("Create Stock")) {
			btnCreateStock.addActionListener(controller);
			pnlButtons.add(btnCreateStock);
		}

		if (user.canAccess("Update Stock")) {
			btnUpdateStock.addActionListener(controller);
			pnlButtons.add(btnUpdateStock);
			btnUpdateStock.setEnabled(false);
		}

		if (user.canAccess("Delete Stock")) {
			btnDeleteStock.addActionListener(controller);
			pnlButtons.add(btnDeleteStock);
			btnDeleteStock.setEnabled(false);
		}

		btnOpenStock.addActionListener(controller);
		pnlButtons.add(btnOpenStock);
		btnOpenStock.setEnabled(false);

		txaStockInfo = new JTextArea("No Stock selected");
		txaStockInfo.setEnabled(false);
		txaStockInfo.setLineWrap(true);
		txaStockInfo.setBackground(Color.LIGHT_GRAY);

		scpStockInfo = new JScrollPane(txaStockInfo);
		scpStockInfo.setPreferredSize(new Dimension(210, 300));
		pnlDashboardRight.add(scpStockInfo);
	}

	public Updater getRepainter() {
		return this.repainter;
	}

	public MainFrame getOwner() {
		return this.owner;
	}

	/**
	 * Observer pattern
	 */
	@Override
	public void update(UpdateSubject updater) {
		for (SquareGraphic sq : squareGraphicList) {
			sq.setState(Service.getWorstState(sq.getStock()));
		}
	}

	public void removeStock() {
		if (selectedSquare != null) {
			pnlDashboardCenter.remove(selectedSquare);
			squareGraphicList.remove(selectedSquare);
			selectedSquare = null;
			this.repaint();
			this.validate();
		}
	}

	public void fillStocks() {
		pnlDashboardCenter.removeAll();
		stocks = new ArrayList<Stock>(Service.getAllStocks());
		Collections.sort(stocks);
		squareGraphicList = new ArrayList<SquareGraphic>();
		selectedSquare = null;

		for (Stock stock : stocks) {
			SquareGraphic sq = new SquareGraphic();
			sq.addMouseListener(selectStockLisneter);
			sq.setPreferredSize(new Dimension(85, 85));
			sq.setStock(stock);
			sq.setState(Service.getWorstState(stock));
			pnlDashboardCenter.add(sq);
			squareGraphicList.add(sq);
		}

		this.repaint();
		this.validate();
	}

	private class SelectStockListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getClickCount() == 2) {
				if (selectedSquare != null) {
					stockOverviewPanel.setStock(selectedSquare.getStock());
					getOwner().navigateTo(stockOverviewPanel);
				}
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
				output += "\n\nUsed storage units:\n"
						+ stock.getStorageUnitsTotal();
				output += "\n\nMax Trays per Storage Unit:\n"
						+ stock.getMaxTraysPerStorageUnit();

				btnUpdateStock.setEnabled(true);
				btnDeleteStock.setEnabled(true);
				btnOpenStock.setEnabled(true);
			}
			else {
				output = "No Stock selected";
				btnUpdateStock.setEnabled(false);
				btnDeleteStock.setEnabled(false);
				btnOpenStock.setEnabled(false);
			}

			txaStockInfo.setText(output);
		}
	}

	private class Controller implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == btnCreateStock) {
				dlgStock.setStock(null);
				dlgStock.setVisible(true);

				if (dlgStock.getIsClosedByOk()) {
					fillStocks();
				}
			}
			else if (src == btnUpdateStock) {
				if (selectedSquare != null && selectedSquare.getStock() != null) {
					dlgStock.setStock(selectedSquare.getStock());
					dlgStock.setVisible(true);

					if (dlgStock.getIsClosedByOk()) {
						fillStocks();
					}
				}
			}
			else if (src == btnDeleteStock) {
				if (selectedSquare != null && selectedSquare.getStock() != null) {
					Stock stock = selectedSquare.getStock();
					int result = JOptionPane.showConfirmDialog(
							DashboardPanel.this,
							"Are you sure you want to delete the Stock \""
									+ stock.getName() + "\"?", "Delete stock",
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						try {
							Service.deleteStock(stock);
							DashboardPanel.this.removeStock();

						}
						catch (StockNotEmptyException ex) {
							JOptionPane.showMessageDialog(DashboardPanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
			else if (src == btnOpenStock) {
				if (selectedSquare != null) {
					stockOverviewPanel.setStock(selectedSquare.getStock());
					getOwner().navigateTo(stockOverviewPanel);
				}
			}
		}

	}
}
