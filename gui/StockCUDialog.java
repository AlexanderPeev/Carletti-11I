package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Stock;
import model.StockType;
import service.Service;
import service.StockNotEmptyException;

/**
 * CU operations for the Stock class.
 * 
 * @author Alexander Peev
 * 
 */
public class StockCUDialog extends JDialog {
	private Stock stock;
	private boolean isClosedByOk;
	private JTextField txfName;
	private JTextField txfCapacity;
	private JTextField txfMaxTrays;
	private JTextField txfUnitsPerRow;
	private JComboBox cbxType;
	private JButton btnOK;
	private JButton btnCancel;
	private Controller controller = new Controller();

	public StockCUDialog() {
		this.setTitle("Stock manager");
		this.setModal(true);
		this.setResizable(true);
		this.setSize(new Dimension(300, 300));
		this.setLocationRelativeTo(this.getRootPane());
		this.setLayout(new BorderLayout(5, 5));

		JPanel pnlMain = new JPanel();
		this.add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new GridLayout(0, 1, 5, 0));

		JLabel lblName = new JLabel("Name:");
		pnlMain.add(lblName);

		txfName = new JTextField();
		pnlMain.add(txfName);

		JLabel lblType = new JLabel("Type:");
		pnlMain.add(lblType);

		cbxType = new JComboBox();
		cbxType.removeAllItems();
		for (StockType type : StockType.values()) {
			cbxType.addItem(type);
		}
		pnlMain.add(cbxType);

		JLabel lblCapacity = new JLabel("Capacity:");
		pnlMain.add(lblCapacity);

		txfCapacity = new JTextField();
		txfCapacity.setText("64");
		pnlMain.add(txfCapacity);

		JLabel lblMaxTrays = new JLabel("Maximum trays per storage unit: ");
		pnlMain.add(lblMaxTrays);

		txfMaxTrays = new JTextField();
		txfMaxTrays.setText("16");
		pnlMain.add(txfMaxTrays);

		JLabel lblUnitsPerRow = new JLabel("Storage units per row:");
		pnlMain.add(lblUnitsPerRow);

		txfUnitsPerRow = new JTextField();
		txfUnitsPerRow.setText("8");
		pnlMain.add(txfUnitsPerRow);

		JPanel pnlSouth = new JPanel();
		pnlSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		this.add(pnlSouth, BorderLayout.SOUTH);

		btnOK = new JButton("OK");
		btnOK.addActionListener(controller);
		pnlSouth.add(btnOK);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(controller);
		pnlSouth.add(btnCancel);
		this.updateDisplay();
	}

	public void updateDisplay() {
		if (this.stock != null) {
			this.setTitle("Edit stock \"" + this.stock.getName() + "\"");
			this.txfName.setText(this.stock.getName());
			this.cbxType.setSelectedItem(stock.getType());
			this.txfCapacity.setText(stock.getCapacity() + "");
			this.txfMaxTrays.setText(stock.getMaxTraysPerStorageUnit() + "");
			this.txfUnitsPerRow.setText(stock.getStorageUnitsPerRow() + "");
		}
		else {
			this.setTitle("New stock");
			this.txfName.setText("New stock");
			this.cbxType.setSelectedItem(StockType.SEMI);
			this.txfCapacity.setText("64");
			this.txfMaxTrays.setText("16");
			this.txfUnitsPerRow.setText("8");
		}
	}

	public void doOk() {
		this.isClosedByOk = true;
		String name = this.txfName.getText().trim();
		Object sel = this.cbxType.getSelectedItem();
		StockType type = StockType.SEMI;
		if (sel != null && sel instanceof StockType) {
			type = (StockType) sel;
		}

		int capacity = 64, maxTrays = 16, unitsPerRow = 8;

		try {
			capacity = Math.abs(Integer.parseInt(this.txfCapacity.getText()
					.trim()));
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Please enter a valid number for the capacity. ", "Error",
					JOptionPane.ERROR_MESSAGE);
			this.isClosedByOk = false;
			return;
		}

		try {
			maxTrays = Math.abs(Integer.parseInt(this.txfMaxTrays.getText()
					.trim()));
		}
		catch (NumberFormatException e) {
			JOptionPane
					.showMessageDialog(
							this,
							"Please enter a valid number for the maximum trays per storage unit. ",
							"Error", JOptionPane.ERROR_MESSAGE);
			this.isClosedByOk = false;
			return;
		}

		try {
			unitsPerRow = Math.abs(Integer.parseInt(this.txfUnitsPerRow
					.getText().trim()));
		}
		catch (NumberFormatException e) {
			JOptionPane
					.showMessageDialog(
							this,
							"Please enter a valid number for the storage units per row. ",
							"Error", JOptionPane.ERROR_MESSAGE);
			this.isClosedByOk = false;
			return;
		}

		if (this.stock != null) {
			try {
				Service.updateStock(this.stock, name, type, capacity, maxTrays,
						unitsPerRow);
			}
			catch (StockNotEmptyException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				this.isClosedByOk = false;
				return;
			}
			JOptionPane.showMessageDialog(this,
					"The stock \"" + this.stock.getName()
							+ "\" was updated successfully. ", "Updated",
					JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			this.stock = Service.createStock(name, type, capacity, maxTrays,
					unitsPerRow);
			JOptionPane.showMessageDialog(this,
					"The stock \"" + this.stock.getName()
							+ "\" was created successfully. ", "Created",
					JOptionPane.INFORMATION_MESSAGE);
		}
		this.setVisible(false);
	}

	public void doCancel() {
		this.isClosedByOk = false;
		this.setVisible(false);
	}

	public Stock getStock() {
		return this.stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
		this.updateDisplay();
	}

	public boolean getIsClosedByOk() {
		return this.isClosedByOk;
	}

	private class Controller implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == btnOK) {
				StockCUDialog.this.doOk();
			}
			else if (src == btnCancel) {
				StockCUDialog.this.doCancel();
			}
		}

	}
}
