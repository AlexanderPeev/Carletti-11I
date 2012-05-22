/**
 * @author Ricardas Risys
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.ProductType;
import model.Stock;
import model.SubProcess;
import service.Service;
import service.SubProcessException;

public class SubProcessCUDialog extends JDialog {

	private ProductTypeCUDialog main = null;
	private final JLabel lblSubProcessName, lblMinTime, lblMinTimeMinutes,
			lblIdealTime, lblIdealTimeMinutes, lblMaxTime, lblMaxTimeMinutes,
			lblAllStocks, lblAssignedStocks;
	private final JTextField txtSubProcessName, txtMinTime, txtIdealTime,
			txtMaxTime;
	private final JButton btnSave, btnClose, btnCancel, btnAllToAssigned,
			btnAssignedToAll;
	private final JList lstAllStocks, lstAssignedStocks;
	private final JScrollPane scpAllStocks, scpAssignedStocks;
	private final Controller controller = new Controller();
	private SubProcess subProcess = null;
	private ProductType productType = null;
	private List<Stock> allStocks = new ArrayList<Stock>(Service.getAllStocks());

	public SubProcessCUDialog(ProductTypeCUDialog main) {
		this.main = main;

		this.setModal(true);
		this.setResizable(false);
		this.setSize(390, 400);
		getContentPane().setLayout(new BorderLayout());

		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		getContentPane().add(pnlMain, BorderLayout.CENTER);

		JPanel pnlName = new JPanel();
		pnlMain.add(pnlName);
		pnlName.setLayout(new FlowLayout(FlowLayout.LEADING));
		lblSubProcessName = new JLabel("Name");
		lblSubProcessName.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlName.add(lblSubProcessName, BorderLayout.NORTH);
		pnlName.setPreferredSize(new Dimension(370, 50));

		txtSubProcessName = new JTextField();
		pnlName.add(txtSubProcessName, BorderLayout.CENTER);
		txtSubProcessName.setPreferredSize(new Dimension(370, 20));

		JPanel pnlTimes = new JPanel();
		pnlTimes.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnlMain.add(pnlTimes);

		JPanel pnlMinTime = new JPanel();
		pnlMinTime.setLayout(new BorderLayout());
		pnlMinTime.setPreferredSize(new Dimension(120, 50));
		pnlTimes.add(pnlMinTime);

		lblMinTime = new JLabel("Min Time:");
		pnlMinTime.add(lblMinTime, BorderLayout.NORTH);

		txtMinTime = new JTextField();
		pnlMinTime.add(txtMinTime, BorderLayout.CENTER);

		lblMinTimeMinutes = new JLabel("in minutes");
		pnlMinTime.add(lblMinTimeMinutes, BorderLayout.SOUTH);

		JPanel pnlIdealTime = new JPanel();
		pnlIdealTime.setLayout(new BorderLayout());
		pnlIdealTime.setPreferredSize(new Dimension(120, 50));
		pnlTimes.add(pnlIdealTime);

		lblIdealTime = new JLabel("Ideal Time:");
		pnlIdealTime.add(lblIdealTime, BorderLayout.NORTH);

		txtIdealTime = new JTextField();
		pnlIdealTime.add(txtIdealTime, BorderLayout.CENTER);

		lblIdealTimeMinutes = new JLabel("in minutes");
		pnlIdealTime.add(lblIdealTimeMinutes, BorderLayout.SOUTH);

		JPanel pnlMaxTime = new JPanel();
		pnlMaxTime.setLayout(new BorderLayout());
		pnlMaxTime.setPreferredSize(new Dimension(120, 50));
		pnlTimes.add(pnlMaxTime);

		lblMaxTime = new JLabel("Max Time:");
		pnlMaxTime.add(lblMaxTime, BorderLayout.NORTH);

		txtMaxTime = new JTextField();
		pnlMaxTime.add(txtMaxTime, BorderLayout.CENTER);

		lblMaxTimeMinutes = new JLabel("in minutes");
		pnlMaxTime.add(lblMaxTimeMinutes, BorderLayout.SOUTH);

		JPanel pnlStocks = new JPanel();
		pnlStocks.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlMain.add(pnlStocks);

		JPanel pnlAllStocks = new JPanel();
		pnlAllStocks.setLayout(new BorderLayout());
		pnlStocks.add(pnlAllStocks);
		pnlAllStocks.setPreferredSize(new Dimension(155, 200));

		lblAllStocks = new JLabel("Available Stocks:");
		pnlAllStocks.add(lblAllStocks, BorderLayout.NORTH);

		lstAllStocks = new JList();
		lstAllStocks.setListData(allStocks.toArray());
		lstAllStocks.addListSelectionListener(controller);
		scpAllStocks = new JScrollPane(lstAllStocks);
		pnlAllStocks.add(scpAllStocks, BorderLayout.CENTER);

		JPanel pnlButtonsAssign = new JPanel();
		pnlButtonsAssign.setLayout(new BoxLayout(pnlButtonsAssign,
				BoxLayout.PAGE_AXIS));
		pnlStocks.add(pnlButtonsAssign);

		btnAllToAssigned = new JButton(">>");
		pnlButtonsAssign.add(btnAllToAssigned);
		btnAllToAssigned.addActionListener(controller);
		btnAllToAssigned.setEnabled(false);

		btnAssignedToAll = new JButton("<<");
		pnlButtonsAssign.add(btnAssignedToAll);
		btnAssignedToAll.addActionListener(controller);
		btnAssignedToAll.setEnabled(false);

		JPanel pnlAssignedStocks = new JPanel();
		pnlAssignedStocks.setLayout(new BorderLayout());
		pnlStocks.add(pnlAssignedStocks);
		pnlAssignedStocks.setPreferredSize(new Dimension(155, 200));

		lblAssignedStocks = new JLabel("Assigned Stocks:");
		pnlAssignedStocks.add(lblAssignedStocks, BorderLayout.NORTH);

		lstAssignedStocks = new JList();
		lstAssignedStocks.addListSelectionListener(controller);
		scpAssignedStocks = new JScrollPane(lstAssignedStocks);
		pnlAssignedStocks.add(scpAssignedStocks, BorderLayout.CENTER);

		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlMain.add(pnlButtons);

		btnSave = new JButton("Apply");
		pnlButtons.add(btnSave);
		btnSave.addActionListener(controller);

		btnClose = new JButton("Save & Close");
		pnlButtons.add(btnClose);
		btnClose.addActionListener(controller);

		btnCancel = new JButton("Cancel");
		pnlButtons.add(btnCancel);
		btnCancel.addActionListener(controller);

		this.setLocationRelativeTo(this.getRootPane());
	}

	public ProductTypeCUDialog getProductTypeCUDialog() {
		return this.main;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public boolean isClosedByOk() {
		return controller.closedByOk;
	}

	public void clearText() {
		txtSubProcessName.setText("");
		txtIdealTime.setText("");
		txtMaxTime.setText("");
		txtMinTime.setText("");
		this.subProcess = null;

		lstAssignedStocks.setListData(new Array[0]);
		allStocks = new ArrayList<Stock>(Service.getAllStocks());
		lstAllStocks.setListData(allStocks.toArray());
	}

	public void fillText(SubProcess subProcess) {
		txtSubProcessName.setText(subProcess.getName());
		txtIdealTime.setText(subProcess.getIdealTime() + "");
		txtMaxTime.setText(subProcess.getMaxTime() + "");
		txtMinTime.setText(subProcess.getMinTime() + "");

		this.subProcess = subProcess;

		lstAssignedStocks.setListData(subProcess.getStocks().toArray());
		allStocks = new ArrayList<Stock>(Service.getAllStocks());
		allStocks.removeAll(subProcess.getStocks());
		lstAllStocks.setListData(allStocks.toArray());
	}

	public boolean saveUpdateSubProcess() {
		try {
			String subProcessName = txtSubProcessName.getText();
			int minTime = Integer.parseInt(txtMinTime.getText());
			int idealTime = Integer.parseInt(txtIdealTime.getText());
			int maxTime = Integer.parseInt(txtMaxTime.getText());

			if (subProcess == null) {
				subProcess = Service.createSubProcess(productType, productType
						.getSubProcesses().size(), subProcessName, minTime,
						idealTime, maxTime);
			} else {
				Service.updateSubProcess(subProcess, subProcess.getOrder(),
						subProcessName, minTime, idealTime, maxTime);
			}

		} catch (SubProcessException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Check the data in fields",
					"Bad Data", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	private class Controller implements ActionListener, ListSelectionListener {
		private boolean closedByOk;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnSave) {
				saveUpdateSubProcess();
				closedByOk = false;
			}
			if (e.getSource() == btnClose) {
				if (saveUpdateSubProcess()) {
					closedByOk = true;
					SubProcessCUDialog.this.setVisible(false);
				}
			}
			if (e.getSource() == btnCancel) {
				closedByOk = false;
				SubProcessCUDialog.this.setVisible(false);
			}

			if (e.getSource() == btnAllToAssigned) {
				Stock stock = (Stock) lstAllStocks.getSelectedValue();
				Service.assignSubProcessToStock(stock, subProcess);
				fillText(subProcess);
			}
			if (e.getSource() == btnAssignedToAll) {
				Stock stock = (Stock) lstAssignedStocks.getSelectedValue();
				Service.unsignSubProcessFromStock(stock, subProcess);
				fillText(subProcess);
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (subProcess != null) {
				if (lstAllStocks.getSelectedValues().length > 0) {
					btnAllToAssigned.setEnabled(true);
				} else {
					btnAllToAssigned.setEnabled(false);
				}

				if (lstAssignedStocks.getSelectedValues().length > 0) {
					btnAssignedToAll.setEnabled(true);
				} else {
					btnAssignedToAll.setEnabled(false);
				}
			}

		}

	}
}
