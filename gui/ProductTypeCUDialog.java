package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.ProductType;
import model.SubProcess;
import service.Service;
import service.SubProcessException;

/**
 * Screen that shows the subprocesses of a product type.
 * 
 * @author Thomas Van Rensburg
 */
public class ProductTypeCUDialog extends JDialog {
	private JTextField txtProdName;
	private JLabel lblProdName;
	private JLabel lblSubprocesses;
	private JList lstSubProcesses;
	private JButton btnCreate, btnUpdate, btnDelete;
	private JScrollPane scrollPane;
	private JButton btnUp;
	private JButton btnDown;
	private JPanel panel;
	private JPanel panel_1;
	private ProductType productType = null;
	private JPanel panel_2;
	private JButton btnSave;
	private JButton btnClose;
	private Controller controller = new Controller();
	private SubProcessCUDialog subProcessCUDialog;

	public ProductTypeCUDialog() {
		setTitle("Create Product Type");
		subProcessCUDialog = new SubProcessCUDialog(this);

		this.setModal(true);
		this.setResizable(true);
		this.setSize(600, 500);
		this.setMinimumSize(new Dimension(500, 450));
		this.setLocationRelativeTo(this.getRootPane());
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 1.0, 1.0 };
		getContentPane().setLayout(gridBagLayout);

		lblProdName = new JLabel("Product Name");
		GridBagConstraints gbc_lblProdName = new GridBagConstraints();
		gbc_lblProdName.insets = new Insets(0, 0, 5, 5);
		gbc_lblProdName.gridx = 0;
		gbc_lblProdName.gridy = 0;
		getContentPane().add(lblProdName, gbc_lblProdName);

		txtProdName = new JTextField();
		GridBagConstraints gbc_txtProdName = new GridBagConstraints();
		gbc_txtProdName.insets = new Insets(0, 0, 5, 5);
		gbc_txtProdName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtProdName.gridx = 0;
		gbc_txtProdName.gridy = 1;
		getContentPane().add(txtProdName, gbc_txtProdName);
		txtProdName.setColumns(10);

		lblSubprocesses = new JLabel("Subprocesses");
		GridBagConstraints gbc_lblSubprocesses = new GridBagConstraints();
		gbc_lblSubprocesses.insets = new Insets(0, 0, 5, 5);
		gbc_lblSubprocesses.gridx = 0;
		gbc_lblSubprocesses.gridy = 2;
		getContentPane().add(lblSubprocesses, gbc_lblSubprocesses);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		gbc_scrollPane.gridheight = 1;

		getContentPane().add(scrollPane, gbc_scrollPane);

		lstSubProcesses = new JList();
		scrollPane.setViewportView(lstSubProcesses);
		lstSubProcesses.setVisibleRowCount(1);
		lstSubProcesses.addListSelectionListener(controller);

		panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(50, 50));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 3;
		getContentPane().add(panel_1, gbc_panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		panel_1.setPreferredSize(new Dimension(210, 210));

		btnCreate = new JButton("Create");
		btnCreate.setPreferredSize(new Dimension(200, 20));
		panel_1.add(btnCreate);
		btnCreate.addActionListener(controller);
		btnCreate.setEnabled(false);

		btnUpdate = new JButton("Update");
		btnUpdate.setPreferredSize(new Dimension(200, 20));
		panel_1.add(btnUpdate);
		btnUpdate.addActionListener(controller);
		btnUpdate.setEnabled(false);

		btnDelete = new JButton("Delete");
		btnDelete.setPreferredSize(new Dimension(200, 20));
		panel_1.add(btnDelete);
		btnDelete.addActionListener(controller);
		btnDelete.setEnabled(false);

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 30));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 4;
		getContentPane().add(panel, gbc_panel);

		btnUp = new JButton("Move Up");
		btnUp.addActionListener(controller);
		btnUp.setMinimumSize(new Dimension(50, 25));
		btnUp.setMaximumSize(new Dimension(50, 25));
		btnUp.setPreferredSize(new Dimension(90, 25));
		btnUp.setEnabled(false);
		panel.add(btnUp);
		btnUp.setHorizontalAlignment(SwingConstants.LEADING);

		btnDown = new JButton("Move Down");
		btnDown.addActionListener(controller);
		btnDown.setMinimumSize(new Dimension(80, 25));
		btnDown.setMaximumSize(new Dimension(80, 25));
		btnDown.setPreferredSize(new Dimension(90, 25));
		btnDown.setEnabled(false);
		panel.add(btnDown);
		btnDown.setHorizontalAlignment(SwingConstants.RIGHT);

		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 5;
		getContentPane().add(panel_2, gbc_panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnSave = new JButton("Save");
		btnSave.setPreferredSize(new Dimension(100, 25));
		panel_2.add(btnSave);
		btnSave.addActionListener(controller);

		btnClose = new JButton("Close");
		btnClose.setPreferredSize(new Dimension(100, 25));
		panel_2.add(btnClose);
		btnClose.addActionListener(controller);
	}

	public ProductType getProductType() {
		return this.productType;
	}

	public void setProductType(ProductType productType) {
		txtProdName.setText(productType.getName());
		this.productType = productType;
		lstSubProcesses.setListData(this.productType.getSubProcesses()
				.toArray());

		btnCreate.setEnabled(true);
	}

	public void clearText() {
		txtProdName.setText("");
		productType = null;
		lstSubProcesses.setListData(new Array[0]);

		btnCreate.setEnabled(false);
		btnUpdate.setEnabled(false);
		btnDelete.setEnabled(false);
		btnUp.setEnabled(false);
		btnDown.setEnabled(false);
	}

	public boolean isClosedByOk() {
		return controller.closedByOk;
	}

	public boolean saveUpdateProductType() {
		String productTypeName = txtProdName.getText();

		if (productType == null) {
			productType = Service.createProductType(productTypeName);
		}
		else {
			Service.updateProductType(productType, productTypeName);
		}

		return true;
	}

	public void fillListSubProcesses() {
		List<SubProcess> sps = productType.getSubProcesses();
		Collections.sort(sps);
		lstSubProcesses.setListData(sps.toArray());
	}

	private class Controller implements ActionListener, ListSelectionListener {

		private boolean closedByOk;

		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == btnSave) {
				if (saveUpdateProductType()) {
					closedByOk = true;
					ProductTypeCUDialog.this.setVisible(false);
				}
			}

			if (e.getSource() == btnClose) {
				closedByOk = true;
				ProductTypeCUDialog.this.setVisible(false);
			}

			if (e.getSource() == btnCreate) {
				subProcessCUDialog.setTitle("Create Sub Process");
				subProcessCUDialog.clearText();
				subProcessCUDialog.setProductType(productType);
				subProcessCUDialog.setVisible(true);
				if (subProcessCUDialog.isClosedByOk()) {
					fillListSubProcesses();
					int lastIndex = lstSubProcesses.getModel().getSize() - 1;
					lstSubProcesses.setSelectedIndex(lastIndex);
					lstSubProcesses.ensureIndexIsVisible(lastIndex);
				}
			}

			if (e.getSource() == btnUpdate) {
				if (lstSubProcesses.getSelectedIndex() == -1) { return; }

				SubProcess subProcess = (SubProcess) lstSubProcesses
						.getSelectedValue();
				subProcessCUDialog.setTitle("Update Sub Process");
				subProcessCUDialog.setProductType(productType);
				subProcessCUDialog.fillText(subProcess);
				subProcessCUDialog.setVisible(true);

				if (subProcessCUDialog.isClosedByOk()) {
					int selectedIndex = lstSubProcesses.getSelectedIndex();
					fillListSubProcesses();
					lstSubProcesses.setSelectedIndex(selectedIndex);
					lstSubProcesses.ensureIndexIsVisible(selectedIndex);
				}
			}

			if (e.getSource() == btnDelete) {
				int index = lstSubProcesses.getSelectedIndex();
				if (index == -1) { return; }

				int answer = JOptionPane.showConfirmDialog(
						ProductTypeCUDialog.this, "Are you sure?",
						"Delete Subprocess", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					SubProcess subProcess = (SubProcess) lstSubProcesses
							.getSelectedValue();
					try {
						Service.deleteSubProcess(productType, subProcess);
						fillListSubProcesses();
					}
					catch (SubProcessException ex) {
						JOptionPane.showMessageDialog(ProductTypeCUDialog.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}

			if (e.getSource() == btnUp) {
				int index = lstSubProcesses.getSelectedIndex();
				Object sel = lstSubProcesses.getSelectedValue();
				if (sel != null && sel instanceof SubProcess) {
					SubProcess sp = (SubProcess) sel;

					Service.moveSubProcessUp(sp, productType);

					lstSubProcesses.setListData(productType.getSubProcesses()
							.toArray());
					lstSubProcesses.setSelectedIndex(index - 1);
				}
			}

			if (e.getSource() == btnDown) {
				int index = lstSubProcesses.getSelectedIndex();
				Object sel = lstSubProcesses.getSelectedValue();
				if (sel != null && sel instanceof SubProcess) {
					SubProcess sp = (SubProcess) sel;
					Service.moveSubProcessDown(sp, productType);
					lstSubProcesses.setListData(productType.getSubProcesses()
							.toArray());
					lstSubProcesses.setSelectedIndex(index + 1);
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (productType != null) {
				if (lstSubProcesses.getSelectedValues().length == 1) {
					btnUpdate.setEnabled(true);
					btnDelete.setEnabled(true);
					btnUp.setEnabled(true);
					btnDown.setEnabled(true);

					int index = lstSubProcesses.getSelectedIndex();
					int totalCount = lstSubProcesses.getModel().getSize();
					int lastIndex = totalCount - 1;
					if (totalCount == 1) {
						btnUp.setEnabled(false);
						btnDown.setEnabled(false);
					}
					else if (index == lastIndex) {
						btnUp.setEnabled(true);
						btnDown.setEnabled(false);
					}
					else if (index == 0) {
						btnUp.setEnabled(false);
						btnDown.setEnabled(true);
					}

				}
				else {
					btnUpdate.setEnabled(false);
					btnDelete.setEnabled(false);
					btnUp.setEnabled(false);
					btnDown.setEnabled(false);
				}

			}
		}

	}
}
