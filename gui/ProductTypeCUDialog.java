package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import service.Service;

import model.ProductType;
import model.SubProcess;

public class ProductTypeCUDialog extends JDialog {
	private JTextField textField_1;
	private JLabel lblNewLabel;
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
	private JButton btnNewButton;
	private Controller controller = new Controller();

	public ProductTypeCUDialog() {
		setTitle("Create Product Type");
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

		lblNewLabel = new JLabel("Product Name");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 0;
		gbc_textField_1.gridy = 1;
		getContentPane().add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

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

		btnUpdate = new JButton("Update");
		btnUpdate.setPreferredSize(new Dimension(200, 20));
		panel_1.add(btnUpdate);

		btnDelete = new JButton("Delete");
		btnDelete.setPreferredSize(new Dimension(200, 20));
		panel_1.add(btnDelete);

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
		panel.add(btnUp);
		btnUp.setHorizontalAlignment(SwingConstants.LEADING);

		btnDown = new JButton("Move Down");
		btnDown.addActionListener(controller);
		btnDown.setMinimumSize(new Dimension(80, 25));
		btnDown.setMaximumSize(new Dimension(80, 25));
		btnDown.setPreferredSize(new Dimension(90, 25));
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
		
		btnNewButton = new JButton("Close");
		btnNewButton.setPreferredSize(new Dimension(100, 25));
		panel_2.add(btnNewButton);
	}


	public ProductType getProductType() {
		return this.productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;

	}
	
	private class Controller implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnUp) {
				Object sel = lstSubProcesses.getSelectedValue();
				if (sel != null && sel instanceof SubProcess) {
					SubProcess sp = (SubProcess)sel;
					Service.moveSubProcessUp(sp, productType);
					lstSubProcesses.setListData(productType.getSubProcesses().toArray());
				}
			}
			else if (e.getSource() == btnDown) {
				Object sel = lstSubProcesses.getSelectedValue();
				if (sel != null && sel instanceof SubProcess) {
					SubProcess sp = (SubProcess)sel;
					Service.moveSubProcessDown(sp, productType);
					lstSubProcesses.setListData(productType.getSubProcesses().toArray());
				}
			}

		}
	}
}
