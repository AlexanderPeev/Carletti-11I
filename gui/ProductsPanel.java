package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.ProductType;
import service.ProductTypeInProductionException;
import service.Service;

/**
 * Creates the product type.
 * 
 * @author Thomas van Rensburg
 */
public class ProductsPanel extends JPanel {

	private MainFrame owner = null;
	private JButton btnCreate;
	private JButton btnEdit;
	private JButton btnDelete;
	private JList lstProductTypes;
	private JScrollPane scrollPane;
	private ProductTypeCUDialog productTypeCUDialog;

	private final Controller controller = new Controller();

	public ProductsPanel(MainFrame owner) {
		this.owner = owner;
		this.setLayout(new BorderLayout(0, 0));

		lstProductTypes = new JList();
		this.productTypeCUDialog = new ProductTypeCUDialog();
		lstProductTypes.addListSelectionListener(controller);

		scrollPane = new JScrollPane(lstProductTypes);
		this.add(scrollPane, BorderLayout.CENTER);

		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		pnlButtons.setPreferredSize(new Dimension(210, 0));

		btnCreate = new JButton("Create");
		btnCreate.setPreferredSize(new Dimension(200, 20));
		btnCreate.addActionListener(controller);

		btnEdit = new JButton("Edit");
		btnEdit.setPreferredSize(new Dimension(200, 20));
		btnEdit.addActionListener(controller);
		btnEdit.setEnabled(false);

		btnDelete = new JButton("Delete");
		btnDelete.setPreferredSize(new Dimension(200, 20));
		btnDelete.addActionListener(controller);
		btnDelete.setEnabled(false);

		pnlButtons.add(btnCreate);
		pnlButtons.add(btnEdit);
		pnlButtons.add(btnDelete);

		this.add(pnlButtons, BorderLayout.EAST);
		fillLstProductTypes();
	}

	public MainFrame getOwner() {
		return this.owner;
	}

	public void fillLstProductTypes() {
		ArrayList<ProductType> types = new ArrayList<ProductType>(
				Service.getAllProductTypes());
		Collections.sort(types);
		lstProductTypes.setListData(types.toArray());
	}

	class Controller implements ActionListener, ListSelectionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnCreate) {
				productTypeCUDialog.setTitle("Create Product Type");
				productTypeCUDialog.clearText();
				productTypeCUDialog.setVisible(true);

				if (productTypeCUDialog.isClosedByOk()) {
					fillLstProductTypes();
					lstProductTypes.setSelectedValue(
							productTypeCUDialog.getProductType(), true);
				}
			}

			if (e.getSource() == btnEdit) {
				Object sel = lstProductTypes.getSelectedValue();

				if (sel != null && sel instanceof ProductType) {
					int selectedIndex = lstProductTypes.getSelectedIndex();
					ProductType p = (ProductType) sel;
					productTypeCUDialog.clearText();
					productTypeCUDialog.setProductType(p);
					productTypeCUDialog.setVisible(true);
					if (productTypeCUDialog.isClosedByOk()) {
						fillLstProductTypes();
						lstProductTypes.setSelectedIndex(selectedIndex);
						lstProductTypes.ensureIndexIsVisible(selectedIndex);
					}
				}
			}

			if (e.getSource() == btnDelete) {
				Object sel = lstProductTypes.getSelectedValue();

				if (sel != null && sel instanceof ProductType) {
					ProductType p = (ProductType) sel;
					int answer = JOptionPane.showConfirmDialog(

					ProductsPanel.this,
							"Are you sure you want to delete " + p.getName()
									+ "?", "Delete product type",
							JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						try {
							Service.deleteProductType(p);
						}
						catch (ProductTypeInProductionException ex) {
							JOptionPane.showMessageDialog(ProductsPanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
						}
						fillLstProductTypes();
					}
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (lstProductTypes.getSelectedValues().length == 1) {
				btnEdit.setEnabled(true);
				btnDelete.setEnabled(true);
			}
			else {
				btnEdit.setEnabled(false);
				btnDelete.setEnabled(false);
			}
		}
	}
}
