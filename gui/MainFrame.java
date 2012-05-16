package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import model.User;
import service.Service;

/**
 * The main frame of the application. It is displayed only when a user is logged
 * in.
 * 
 * @author Alexander Peev
 * 
 */
public class MainFrame extends JFrame {
	private JMenuItem mntmDashboard, mntmProducts, mntmPercentageOfWaste,
			mntmAveragePickingTimes, mntmCreateTrays, mntmLogout;
	NotificationsPanel pnlNotifications = new NotificationsPanel(this);
	DashboardPanel pnlDashboard = new DashboardPanel(this);
	ProductsPanel pnlProducts = new ProductsPanel(this);
	TraysCDialog dlgTraysCreate = new TraysCDialog(this);
	StatisticsAveragePickingTimesPanel pnlStatisticsAveragePickingTimes = new StatisticsAveragePickingTimesPanel(
			this);
	StatisticsPercentageOfWastePanel pnlStatisticsPercentageOfWaste = new StatisticsPercentageOfWastePanel(
			this);
	Controller controller = new Controller();
	private JPanel current = null;

	public MainFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		User user = Service.getCurrentUser();
		this.setTitle("Carletti Production Manager - " + user.getUsername()
				+ " (" + user.getGroup() + ")");
		this.setResizable(true);
		this.setSize(600, 500);
		this.setMinimumSize(new Dimension(500, 450));
		this.setLocationRelativeTo(this.getRootPane());

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		if (user.canAccess("Dashboard")) {
			mntmDashboard = new JMenuItem("Dashboard");
			mntmDashboard.setMnemonic('D');
			mntmDashboard.addActionListener(controller);
			menuBar.add(mntmDashboard);
		}

		if (user.canAccess("Products")) {
			mntmProducts = new JMenuItem("Products");
			mntmProducts.setMnemonic('P');
			mntmProducts.addActionListener(controller);
			menuBar.add(mntmProducts);
		}

		if (user.canAccess("Statistics")) {
			JMenu mnStatistics = new JMenu("Statistics");
			mnStatistics.setMnemonic('S');
			menuBar.add(mnStatistics);

			mntmPercentageOfWaste = new JMenuItem("Percentage of Waste");
			mntmPercentageOfWaste.setMnemonic('W');
			mntmPercentageOfWaste.addActionListener(controller);
			mnStatistics.add(mntmPercentageOfWaste);

			mntmAveragePickingTimes = new JMenuItem("Average Picking Times");
			mntmAveragePickingTimes.setMnemonic('A');
			mntmAveragePickingTimes.addActionListener(controller);
			mnStatistics.add(mntmAveragePickingTimes);
		}

		if (user.canAccess("Create Trays")) {
			mntmCreateTrays = new JMenuItem("Create Trays");
			mntmCreateTrays.setMnemonic('T');
			mntmCreateTrays.addActionListener(controller);
			menuBar.add(mntmCreateTrays);
		}

		if (user.canAccess("Logout")) {
			mntmLogout = new JMenuItem("Logout");
			mntmLogout.setMnemonic('L');
			mntmLogout.addActionListener(controller);
			menuBar.add(mntmLogout);
		}

		this.add(pnlNotifications, BorderLayout.NORTH);

		this.navigateTo(pnlDashboard);
	}

	public void navigateTo(JPanel panel) {
		if (current != null) this.remove(current);
		this.add(panel, BorderLayout.CENTER);
		current = panel;
		this.indicateSelection(panel);
		this.repaint();
		this.validate();
	}

	private void indicateSelection(JPanel panel) {
		if (panel == this.pnlDashboard) {
			this.mntmDashboard.setEnabled(false);
		}
		else {
			this.mntmDashboard.setEnabled(true);
		}
		if (panel == this.pnlProducts) {
			this.mntmProducts.setEnabled(false);
		}
		else {
			this.mntmProducts.setEnabled(true);
		}
		if (panel == this.pnlStatisticsAveragePickingTimes) {
			this.mntmAveragePickingTimes.setEnabled(false);
		}
		else {
			this.mntmAveragePickingTimes.setEnabled(true);
		}
		if (panel == this.pnlStatisticsPercentageOfWaste) {
			this.mntmPercentageOfWaste.setEnabled(false);
		}
		else {
			this.mntmPercentageOfWaste.setEnabled(true);
		}
	}

	public DashboardPanel getDashboardPanel() {
		return this.pnlDashboard;
	}

	public ProductsPanel getProductsPanel() {
		return this.pnlProducts;
	}

	public TraysCDialog getTraysCDialog() {
		return this.dlgTraysCreate;
	}

	public StatisticsAveragePickingTimesPanel getStatisticsAveragePickingTimesPanel() {
		return this.pnlStatisticsAveragePickingTimes;
	}

	public StatisticsPercentageOfWastePanel getStatisticsPercentageOfWastePanel() {
		return this.pnlStatisticsPercentageOfWaste;
	}

	private class Controller implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == MainFrame.this.mntmDashboard) MainFrame.this
					.navigateTo(MainFrame.this.getDashboardPanel());
			else if (src == MainFrame.this.mntmProducts) MainFrame.this
					.navigateTo(MainFrame.this.getProductsPanel());
			else if (src == MainFrame.this.mntmCreateTrays) MainFrame.this
					.getTraysCDialog().setVisible(true);
			else if (src == MainFrame.this.mntmAveragePickingTimes) MainFrame.this
					.navigateTo(MainFrame.this
							.getStatisticsAveragePickingTimesPanel());
			else if (src == MainFrame.this.mntmPercentageOfWaste) MainFrame.this
					.navigateTo(MainFrame.this
							.getStatisticsPercentageOfWastePanel());
			else if (src == MainFrame.this.mntmLogout) {
				MainFrame.this
						.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				LoginFrame frame = new LoginFrame();
				frame.setVisible(true);
				MainFrame.this.setVisible(false);
			}
		}

	}
}