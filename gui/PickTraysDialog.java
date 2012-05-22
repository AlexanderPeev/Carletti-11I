package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Stock;
import service.Service;

public class PickTraysDialog extends JDialog {
	private Set<Stock> stocks = new HashSet<Stock>();
	private boolean wasAccepted;
	private JList lstStocks;
	private JButton btnOk;
	private JButton btnCancel;
	private JTextField txfDate;
	private Stock stock;
	private boolean requiresStock;
	private Controller controller = new Controller();

	public PickTraysDialog() {
		this.setTitle("Pick trays");
		this.setModal(true);
		this.setResizable(true);
		this.setSize(new Dimension(300, 300));
		this.setLocationRelativeTo(this.getRootPane());
		this.setLayout(new BorderLayout(0, 0));

		JPanel pnlNorth = new JPanel();
		this.add(pnlNorth, BorderLayout.NORTH);
		pnlNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblDate = new JLabel("Date:");
		pnlNorth.add(lblDate);

		txfDate = new JTextField();

		txfDate.setPreferredSize(new Dimension(120, 20));
		pnlNorth.add(txfDate);

		JScrollPane scpCenter = new JScrollPane();
		this.add(scpCenter, BorderLayout.CENTER);

		lstStocks = new JList();
		lstStocks.addListSelectionListener(controller);
		lstStocks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scpCenter.setViewportView(lstStocks);

		JPanel pnlSouth = new JPanel();
		this.add(pnlSouth, BorderLayout.SOUTH);
		pnlSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

		btnOk = new JButton("OK");
		btnOk.setEnabled(false);
		btnOk.addActionListener(controller);
		pnlSouth.add(btnOk);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(controller);
		pnlSouth.add(btnCancel);
		this.updateDisplay();
	}

	public void updateDisplay() {
		this.wasAccepted = false;
		this.lstStocks.setListData(this.stocks.toArray());
		this.txfDate.setText(Service.dateToString(new Date(System
				.currentTimeMillis())));
		if (this.requiresStock) {
			this.btnOk.setEnabled(false);
		}
		else this.btnOk.setEnabled(true);
	}

	public boolean getWasAccepted() {
		return this.wasAccepted;
	}

	public Set<Stock> getStocks() {
		return new HashSet<Stock>(this.stocks);
	}

	public void setStocks(Collection<Stock> stocks) {
		this.stocks.addAll(stocks);
		this.updateDisplay();
	}

	public void clearStocks() {
		this.stocks.clear();
	}

	public Date getDate() {
		return Service.stringToDate(this.txfDate.getText());
	}

	public Stock getStock() {
		return this.stock;
	}

	public boolean getRequiresStock() {
		return this.requiresStock;
	}

	public void setRequiresStock(boolean requiresStock) {
		this.requiresStock = requiresStock;
		this.updateDisplay();
	}

	private class Controller implements ActionListener, ListSelectionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == btnOk) {
				Object sel = lstStocks.getSelectedValue();
				if (sel != null && sel instanceof Stock) {
					stock = (Stock) sel;
					wasAccepted = true;
					Updater.getInstance().notifyObservers();
					setVisible(false);
				}
				else if (!requiresStock) {
					wasAccepted = true;
					Updater.getInstance().notifyObservers();
					setVisible(false);
				}
			}
			else if (src == btnCancel) {
				wasAccepted = false;
				setVisible(false);
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				Object sel = lstStocks.getSelectedValue();
				if (sel != null && sel instanceof Stock) {
					btnOk.setEnabled(true);
				}
				else {
					if (requiresStock) btnOk.setEnabled(false);
					else btnOk.setEnabled(true);
				}
			}
		}

	}
}
