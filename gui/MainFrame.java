package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import model.User;
import service.Service;

public class MainFrame extends JFrame {
	private JMenuItem mntmDashboard, mntmProducts, mntmPercentageOfWaste,
			mntmAveragePickingTimes, mntmCreateTrays, mntmLogout;
	NotificationsPanel notificationsPanel = new NotificationsPanel(this);
	DashboardPanel dashboardPanel = new DashboardPanel(this);
	ProductsPanel productsPanel = new ProductsPanel(this);
	TraysCPanel traysCPanel = new TraysCPanel(this);
	StatisticsAveragePickingTimesPanel statisticsAveragePickingTimesPanel = new StatisticsAveragePickingTimesPanel(
			this);
	StatisticsPercentageOfWastePanel statisticsPercentageOfWastePanel = new StatisticsPercentageOfWastePanel(
			this);
	Controller controller = new Controller();

	public MainFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		User user = Service.getCurrentUser();
		this.setTitle("Carletti Production Manager - " + user.getUsername()
				+ " (" + user.getGroup() + ")");
		this.setResizable(false);
		this.setSize(600, 500);
		this.setLocationRelativeTo(this.getRootPane());

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mntmDashboard = new JMenuItem("Dashboard");
		mntmDashboard.setMnemonic('D');
		mntmDashboard.addActionListener(controller);
		menuBar.add(mntmDashboard);

		mntmProducts = new JMenuItem("Products");
		mntmProducts.setMnemonic('P');
		mntmProducts.addActionListener(controller);
		menuBar.add(mntmProducts);

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

		mntmCreateTrays = new JMenuItem("Create Trays");
		mntmCreateTrays.setMnemonic('T');
		mntmCreateTrays.addActionListener(controller);
		menuBar.add(mntmCreateTrays);

		mntmLogout = new JMenuItem("Logout");
		mntmLogout.setMnemonic('L');
		mntmLogout.addActionListener(controller);
		menuBar.add(mntmLogout);

		this.add(notificationsPanel, BorderLayout.NORTH);

		this.navigateTo(dashboardPanel);
	}

	public void navigateTo(JPanel panel) {
		this.add(panel, BorderLayout.CENTER);
	}

	public DashboardPanel getDashboardPanel() {
		return this.dashboardPanel;
	}

	public ProductsPanel getProductsPanel() {
		return this.productsPanel;
	}

	public TraysCPanel getTraysCPanel() {
		return this.traysCPanel;
	}

	public StatisticsAveragePickingTimesPanel getStatisticsAveragePickingTimesPanel() {
		return this.statisticsAveragePickingTimesPanel;
	}

	public StatisticsPercentageOfWastePanel getStatisticsPercentageOfWastePanel() {
		return this.statisticsPercentageOfWastePanel;
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
					.navigateTo(MainFrame.this.getTraysCPanel());
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
