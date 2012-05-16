package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.ProductType;



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
		
		scrollPane = new JScrollPane(lstProductTypes);
		this.add(scrollPane, BorderLayout.CENTER);
		

		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		pnlButtons.setPreferredSize(new Dimension(210, 0));

		btnCreate = new JButton("Create");
		btnCreate.setPreferredSize(new Dimension(200, 20));
		btnCreate.addActionListener(controller);
		//this.add(btnCreate);

		btnEdit = new JButton("Edit");
		btnEdit.setPreferredSize(new Dimension(200, 20));
		btnEdit.addActionListener(controller);
		//this.add(btnEdit);

		btnDelete = new JButton("Delete");
		btnDelete.setPreferredSize(new Dimension(200, 20));
		btnDelete.addActionListener(controller);
		//this.add(btnDelete);

		// Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		// Component rigidArea2 = Box.createRigidArea(new Dimension(20, 20));

		pnlButtons.add(btnCreate);
		// pnlButtons.add(rigidArea);
		pnlButtons.add(btnEdit);
		// pnlButtons.add(rigidArea2);
		pnlButtons.add(btnDelete);

		this.add(pnlButtons, BorderLayout.EAST);
	}

	public MainFrame getOwner() {
		return this.owner;
	}
	
	class Controller implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnCreate) {
				productTypeCUDialog.setProductType(null);
				productTypeCUDialog.setVisible(true);
				System.out.println("create pressed");
			}
			else if (e.getSource() == btnEdit) {
				Object sel = lstProductTypes.getSelectedValue();
				if(sel != null && sel instanceof ProductType){
					ProductType p = (ProductType) sel;
					productTypeCUDialog.setProductType(p);
					productTypeCUDialog.setVisible(true);
				}
			}
			else if (e.getSource() == btnDelete) {
				Object sel = lstProductTypes.getSelectedValue();
				if(sel != null && sel instanceof ProductType){
					ProductType p = (ProductType) sel;
					p.getName(); //TODO: Finish Delete
				}
			}
		}
	}
}
