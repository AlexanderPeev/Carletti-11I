package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.InconsistencyException;
import model.OutOfStockSpaceException;
import model.ProductType;
import model.Stock;
import model.SubProcess;
import service.Service;

/**
 * @author Tsvetomir Iliev
 */
public class TraysCDialog extends JDialog {

	private MainFrame main = null;
	private JList lstProduct;
	private JButton btnCreate;
	private JPanel pnlButton;
	private JTextField txfAmount;
	private JScrollPane jScrollPane1;
	private JList lstStock;
	private JLabel lblAmount;
	private Controller controller = new Controller();

	public TraysCDialog(MainFrame main) {
		this.main = main;
		this.setModal(true);
		this.setTitle("Create Trays");
		this.setMinimumSize(new Dimension(300, 300));
		BorderLayout thisLayout = new BorderLayout();
		this.setResizable(true);
		getContentPane().setLayout(thisLayout);

		pnlButton = new JPanel();

		getContentPane().add(pnlButton, BorderLayout.EAST);
		pnlButton.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		pnlButton.setPreferredSize(new Dimension(210, 0));

		lblAmount = new JLabel("Amount of Trays");
		lblAmount.setPreferredSize(new java.awt.Dimension(118, 20));
		pnlButton.add(lblAmount);

		txfAmount = new JTextField();
		txfAmount.setPreferredSize(new Dimension(200, 20));
		pnlButton.add(txfAmount);
		lstStock = new JList();

		BoxLayout lstStockLayout = new BoxLayout(lstStock,
				javax.swing.BoxLayout.Y_AXIS);
		lstStock.setLayout(lstStockLayout);
		pnlButton.add(lstStock);
		lstStock.addListSelectionListener(controller);
		lstStock.setPreferredSize(new java.awt.Dimension(192, 75));
		lstStock.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));

		btnCreate = new JButton("Create");
		btnCreate.addActionListener(controller);
		pnlButton.add(btnCreate);
		btnCreate.setPreferredSize(new Dimension(200, 20));

		jScrollPane1 = new JScrollPane(lstProduct);

		jScrollPane1 = new JScrollPane();
		getContentPane().add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.setPreferredSize(new java.awt.Dimension(494, 479));

		lstProduct = new JList();
		lstProduct.addListSelectionListener(controller);
		jScrollPane1.setViewportView(lstProduct);

		this.setSize(710, 512);
		this.setLocationRelativeTo(this.getRootPane());
		btnCreate.setEnabled(false);
		refillList();
	}

	public void refillList() {
		List<ProductType> types = new ArrayList<ProductType>(
				Service.getAllProductTypes());
		Collections.sort(types);
		lstProduct.setListData(types.toArray());
	}

	public MainFrame getMain() {
		return this.main;
	}

	private class Controller implements ActionListener, ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() == lstStock) {
				if (lstStock.getSelectedValue() != null
						&& lstStock.getSelectedValue() instanceof Stock) btnCreate
						.setEnabled(true);
			}
			else if (e.getSource() == lstProduct) {
				Object sel = lstProduct.getSelectedValue();
				if (sel != null && sel instanceof ProductType) {
					ProductType p = (ProductType) sel;
					List<SubProcess> sps = p.getSubProcesses();
					if (sps != null && sps.size() > 0 && sps.get(0) != null) {
						lstStock.setListData(sps.get(0).getStocks().toArray());
					}
					else lstStock.setListData(new Object[0]);
				}
				else {
					lstStock.setListData(new Object[0]);
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnCreate) {
				int amount = 0;
				try {
					amount = Integer.parseInt(txfAmount.getText().trim());
				}
				catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(TraysCDialog.this,
							"Please enter a valid number for the amount. ",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				ProductType productType = (ProductType) lstProduct
						.getSelectedValue();
				Stock stock = (Stock) lstStock.getSelectedValue();
				if (productType != null && stock != null && amount > 0) {

					try {
						Service.createTrays(productType, stock, amount);
						JOptionPane.showMessageDialog(TraysCDialog.this, amount
								+ " trays were created and deposited at "
								+ stock.getName() + ". ", "Created",
								JOptionPane.INFORMATION_MESSAGE);
						setVisible(false);
					}
					catch (OutOfStockSpaceException ex) {
						JOptionPane.showMessageDialog(TraysCDialog.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					catch (InconsistencyException ex) {
						JOptionPane.showMessageDialog(TraysCDialog.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}

			}
		}

	}
}