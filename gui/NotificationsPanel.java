package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Stock;
import model.Tray;
import service.Service;

/**
 * Displays notifications about stocks getting full, about aging and wasted
 * trays. The panel hides automatically if there is nothing important to
 * display.
 * 
 * @author Alexander Peev
 * 
 */
public class NotificationsPanel extends JPanel implements UpdateObserver {

	private MainFrame owner = null;
	private JLabel lblStocks;
	private JLabel lblWasted;
	private JLabel lblExpiring;
	private Updater updater;

	public NotificationsPanel(MainFrame owner) {
		this.owner = owner;
		this.setPreferredSize(new Dimension(0, 50));
		this.setLayout(new GridLayout(1, 3, 5, 0));
		this.updater = Updater.getInstance();
		this.updater.registerObserver(this);

		JPanel pnlStocks = new JPanel();
		pnlStocks.setLayout(new BorderLayout());
		add(pnlStocks);

		lblStocks = new JLabel("");
		pnlStocks.add(lblStocks);
		pnlStocks.add(Box.createRigidArea(new Dimension(5, 5)),
				BorderLayout.WEST);

		JPanel pnlExpiring = new JPanel();
		pnlExpiring.setLayout(new BorderLayout());
		add(pnlExpiring);

		lblExpiring = new JLabel("");
		pnlExpiring.add(lblExpiring);

		JPanel pnlWasted = new JPanel();
		pnlWasted.setLayout(new BorderLayout());
		add(pnlWasted);

		lblWasted = new JLabel("");
		pnlWasted.add(lblWasted);
		pnlWasted.add(Box.createRigidArea(new Dimension(5, 5)),
				BorderLayout.EAST);

		this.checkStatus();
	}

	public MainFrame getOwner() {
		return this.owner;
	}

	public void checkStatus() {
		boolean nothingToReport = true;
		Set<Stock> critical = Service.getCriticalStocks(75);
		if (critical != null && critical.size() > 0) {
			if (critical.size() <= 1) {
				String name = "Unnamed";
				for (Stock s : critical) {
					if (s != null && s.getName() != null
							&& s.getName().trim().length() > 0) {
						name = s.getName();
						break;
					}
				}
				lblStocks
						.setText("<html><strong style=\"color:#777700;\">We are in a bit of a pickle</strong>: one of your stocks ("
								+ name + ") is running out of space. </html>");
			}
			else {
				String names = "";
				for (Stock s : critical) {
					if (s != null && s.getName() != null
							&& s.getName().trim().length() > 0) {
						if (names.length() > 0) names += ", ";
						names += s.getName();
					}
					else {
						if (names.length() > 0) names += ", ";
						names += "Unnamed";
					}
				}
				lblStocks
						.setText("<html><strong style=\"color:red;\">You are in a world of hurt</strong>: "
								+ critical.size()
								+ " of your stocks ("
								+ names
								+ ") are running out of space. </html>");
			}
			nothingToReport = false;
		}
		else {
			lblStocks
					.setText("<html><strong style=\"color:green;\">Your storage is in tip-top shape</strong>: there are no Stocks running out of space. </html>");
		}

		Set<Tray> expiring = Service.getExpiringTrays();
		if (expiring != null && expiring.size() > 0) {
			Map<Stock, Integer> stocks = new HashMap<Stock, Integer>();
			for (Tray tray : expiring) {
				if (tray != null && tray.getStorageUnit() != null
						&& tray.getStorageUnit().getStock() != null) {
					Stock stock = tray.getStorageUnit().getStock();
					if (stocks.containsKey(stock)) {
						stocks.put(stock, stocks.get(stock) + 1);
					}
					else {
						stocks.put(stock, 1);
					}
				}
				else {
					if (stocks.containsKey(null)) {
						stocks.put(null, stocks.get(null) + 1);
					}
					else {
						stocks.put(null, 1);
					}
				}
			}
			String names = "";
			for (Stock s : stocks.keySet()) {
				if (s != null && s.getName() != null
						&& s.getName().trim().length() > 0) {
					if (names.length() > 0) names += ", ";
					names += s.getName();
					names += " - " + stocks.get(s);
				}
				else if (s == null) {
					if (names.length() > 0) names += ", ";
					names += "Unknown stock";
					names += " - " + stocks.get(s);
				}
			}
			lblExpiring
					.setText("<html><strong style=\"color:#777700;\">The situation is getting hairy</strong>: the following stocks have aging trays: "
							+ names + ". </html>");
			nothingToReport = false;
		}
		else {
			lblExpiring
					.setText("<html><strong style=\"color:green;\">There is no problem with expiring trays</strong>: there are no expiring trays at the moment. </html>");
		}
		Set<Tray> wasted = Service.getWastedTrays();
		if (wasted != null && wasted.size() > 0) {
			Map<Stock, Integer> stocks = new HashMap<Stock, Integer>();
			for (Tray tray : wasted) {
				if (tray != null && tray.getStorageUnit() != null
						&& tray.getStorageUnit().getStock() != null) {
					Stock stock = tray.getStorageUnit().getStock();
					if (stocks.containsKey(stock)) {
						stocks.put(stock, stocks.get(stock) + 1);
					}
					else {
						stocks.put(stock, 1);
					}
				}
				else {
					if (stocks.containsKey(null)) {
						stocks.put(null, stocks.get(null) + 1);
					}
					else {
						stocks.put(null, 1);
					}
				}
			}
			String names = "";
			for (Stock s : stocks.keySet()) {
				if (s != null && s.getName() != null
						&& s.getName().trim().length() > 0) {
					if (names.length() > 0) names += ", ";
					names += s.getName();
					names += " - " + stocks.get(s);
				}
				else if (s == null) {
					if (names.length() > 0) names += ", ";
					names += "Unknown stock";
					names += " - " + stocks.get(s);
				}
			}
			lblWasted
					.setText("<html><strong style=\"color:red;\">The reality is getting very sad</strong>: the following stocks have wasted trays: "
							+ names + ". </html>");
			nothingToReport = false;
		}
		else {
			lblWasted
					.setText("<html><strong style=\"color:green;\">There is no problem with wasted trays</strong>: there are no wasted trays at the moment. </html>");
		}

		if (nothingToReport) {
			this.setPreferredSize(new Dimension(0, 0));
		}
		else {
			this.setPreferredSize(new Dimension(0, 50));
		}
	}

	@Override
	public void update(UpdateSubject repainter) {
		this.checkStatus();
	}
}
