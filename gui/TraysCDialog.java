package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

public class TraysCDialog extends JDialog {

	private MainFrame main = null;
	private JList lstProduct;
	private JButton btnCreate;
	private JPanel pnlButton;
	private JTextField txfAmount;
	private JScrollPane jScrollPane1;
	private JList lstStock;
	private JLabel lblAmount;

	public TraysCDialog(MainFrame main) {
		this.main = main;
		this.setModal(true);
		this.setTitle("Create Trays");
		BorderLayout thisLayout = new BorderLayout();
		this.setResizable(true);
		getContentPane().setLayout(thisLayout);

		pnlButton = new JPanel();

		getContentPane().add(pnlButton, BorderLayout.EAST);
		pnlButton.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		pnlButton.setPreferredSize(new Dimension(210, 0));

		lblAmount = new JLabel("Amount  of Trays");
		lblAmount.setPreferredSize(new Dimension(200, 20));
		pnlButton.add(lblAmount);

		txfAmount = new JTextField();
		txfAmount.setPreferredSize(new Dimension(200, 20));
		pnlButton.add(txfAmount);

		btnCreate = new JButton("Create");
		btnCreate.setPreferredSize(new Dimension(200, 20));
		pnlButton.add(btnCreate);

		ListModel lstStockModel = new DefaultComboBoxModel(new String[] {
				"Item One", "Item Two" });
		lstStock = new JList();
		BoxLayout lstStockLayout = new BoxLayout(lstStock,
				javax.swing.BoxLayout.Y_AXIS);
		lstStock.setLayout(lstStockLayout);
		pnlButton.add(lstStock);
		lstStock.setModel(lstStockModel);
		lstStock.setPreferredSize(new java.awt.Dimension(192, 46));

		jScrollPane1 = new JScrollPane(lstProduct);

		jScrollPane1 = new JScrollPane();
		getContentPane().add(jScrollPane1, BorderLayout.CENTER);
		jScrollPane1.setPreferredSize(new java.awt.Dimension(494, 479));

		ListModel jList1Model = new DefaultComboBoxModel(new String[] {
				"Item One", "Item Two" });
		lstProduct = new JList();
		jScrollPane1.setViewportView(lstProduct);
		lstProduct.setModel(jList1Model);
		// lstProduct.setPreferredSize(new java.awt.Dimension(494, 452));
		this.setSize(710, 512);
		this.setLocationRelativeTo(this.getRootPane());
	}

	public MainFrame getMain() {
		return this.main;
	}
}
