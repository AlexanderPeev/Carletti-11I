package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.InconsistencyException;
import model.OutOfStockSpaceException;
import model.ProductType;
import model.State;
import model.Stock;
import model.StorageUnit;
import model.Tray;
import service.PickTooEarlyException;
import service.PickTooLateException;
import service.Service;
import service.StockNotCompatibleException;

/**
 * A panel for displaying the contents of a Stock object. It provides a visual
 * representation of all storage units, and means to select and pick trays.
 * 
 * @author Alexander Peev
 * 
 */
public class StockOverviewPanel extends JPanel implements ClickListener,
		TraySelectionChangedListener, UpdateObserver {

	private DashboardPanel owner = null;
	private JPanel pnlStorageUnits, pnlZoom;
	private JButton btnClearSelection, btnPickSelected, btnRemoveSelected,
			btnSelectAll, btnSelectUnit;
	private JLabel lblSelection;
	private Stock stock;
	private List<StorageUnitDisplayPanel> compactDisplays;
	private StorageUnitDisplayPanel selectedCompactDisplay = null;
	private StorageUnitDisplayPanel zoomDisplay = new StorageUnitDisplayPanel(
			null, DisplayMode.FULL);
	private Controller controller = new Controller();
	private Updater updater;
	private PickTraysDialog dlgPickTrays = new PickTraysDialog();

	public StockOverviewPanel(DashboardPanel owner) {
		this.owner = owner;
		setLayout(new BorderLayout(0, 0));
		StorageUnitDisplayPanel.addTraySelectionChangedListener(this);

		this.compactDisplays = new ArrayList<StorageUnitDisplayPanel>();
		this.updater = Updater.getInstance();
		this.updater.registerObserver(this);

		JPanel pnlEast = new JPanel(), pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		add(pnlEast, BorderLayout.EAST);
		pnlEast.setLayout(new BoxLayout(pnlEast, BoxLayout.Y_AXIS));
		pnlEast.setPreferredSize(new Dimension(210, 0));
		pnlZoom = new JPanel();
		pnlZoom.setLayout(new BorderLayout());
		pnlZoom.setPreferredSize(new Dimension(210, 250));
		pnlEast.add(pnlButtons);
		pnlEast.add(pnlZoom);
		btnPickSelected = new JButton("Pick Selected");
		this.btnPickSelected.setEnabled(false);
		this.btnPickSelected.setPreferredSize(new Dimension(200, 20));
		this.btnPickSelected.addActionListener(controller);
		pnlButtons.add(btnPickSelected);

		btnRemoveSelected = new JButton("Remove Selected");
		this.btnRemoveSelected.setEnabled(false);
		this.btnRemoveSelected.setPreferredSize(new Dimension(200, 20));
		this.btnRemoveSelected.addActionListener(controller);
		pnlButtons.add(btnRemoveSelected);

		btnClearSelection = new JButton("Clear Selection");
		this.btnClearSelection.setEnabled(false);
		this.btnClearSelection.setPreferredSize(new Dimension(200, 20));
		this.btnClearSelection.addActionListener(controller);
		pnlButtons.add(btnClearSelection);

		btnSelectAll = new JButton("Select All");
		this.btnSelectAll.setPreferredSize(new Dimension(95, 20));
		this.btnSelectAll.addActionListener(controller);
		pnlButtons.add(btnSelectAll);

		btnSelectUnit = new JButton("Select Unit");
		this.btnSelectUnit.setEnabled(false);
		this.btnSelectUnit.setPreferredSize(new Dimension(95, 20));
		this.btnSelectUnit.addActionListener(controller);
		pnlButtons.add(btnSelectUnit);

		lblSelection = new JLabel("No Trays Selected");
		this.lblSelection.setPreferredSize(new Dimension(95, 20));
		pnlButtons.add(lblSelection);

		pnlButtons.setPreferredSize(new Dimension(210, 135));

		pnlStorageUnits = new JPanel();
		pnlStorageUnits.setLayout(new GridLayout(1, 0, 0, 0));
		pnlStorageUnits.setBackground(Color.BLACK);

		JScrollPane scpMain = new JScrollPane(pnlStorageUnits);
		add(scpMain, BorderLayout.CENTER);
	}

	public Updater getRepainter() {
		return this.updater;
	}

	public void updateDisplay() {
		this.pnlStorageUnits.removeAll();
		if (this.stock != null) {
			int columns = this.stock.getStorageUnitsPerRow();
			this.pnlStorageUnits.setLayout(new GridLayout(0, columns, 5, 5));
			StorageUnitDisplayPanel.clearSelection();
			for (StorageUnitDisplayPanel display : this.compactDisplays) {
				display.detatch();
				display.removeClickListener(this);
			}
			this.compactDisplays.clear();
			Dimension minSize = new Dimension(120, 120);
			for (StorageUnit storageUnit : this.stock.getStorageUnits()) {
				StorageUnitDisplayPanel display = new StorageUnitDisplayPanel(
						storageUnit, DisplayMode.COMPACT);
				this.compactDisplays.add(display);
				this.pnlStorageUnits.add(display);
				display.setPreferredSize(minSize);
				display.addClickListener(this);
				display.setBackground(Color.BLACK);
			}
		}
	}

	public Stock getStock() {
		return this.stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
		this.updateDisplay();
	}

	public DashboardPanel getOwner() {
		return this.owner;
	}

	@Override
	public void onClick(ClickProvider provider) {
		if (this.compactDisplays.contains(provider)) {
			StorageUnitDisplayPanel display = this.compactDisplays
					.get(this.compactDisplays.indexOf(provider));
			StorageUnit unit = display.getStorageUnit();
			pnlZoom.remove(this.zoomDisplay);
			if (this.selectedCompactDisplay != null) this.selectedCompactDisplay
					.setBorder(null);
			this.selectedCompactDisplay = display;
			if (this.selectedCompactDisplay != null) {
				this.btnSelectUnit.setEnabled(true);
			}
			else {
				this.btnSelectUnit.setEnabled(false);
			}
			this.zoomDisplay = new StorageUnitDisplayPanel(unit,
					DisplayMode.FULL);
			display.setBorder(BorderFactory.createMatteBorder(1, 1, 4, 1,
					Color.LIGHT_GRAY));
			pnlZoom.add(this.zoomDisplay, BorderLayout.CENTER);

			this.validate();
		}
	}

	public void selectAll() {
		if (this.stock == null) return;
		if (selectedCompactDisplay != null
				&& selectedCompactDisplay.getStorageUnit() != null) {
			for (Tray tray : selectedCompactDisplay.getStorageUnit().getTrays()) {
				StorageUnitDisplayPanel.addSelectedTray(tray);
			}
		}
		for (StorageUnit unit : this.stock.getStorageUnits()) {
			for (Tray tray : unit.getTrays()) {
				StorageUnitDisplayPanel.addSelectedTray(tray);
			}
		}
	}

	public void selectUnit() {
		if (this.stock == null) return;
		if (selectedCompactDisplay != null
				&& selectedCompactDisplay.getStorageUnit() != null) {
			for (Tray tray : selectedCompactDisplay.getStorageUnit().getTrays()) {
				StorageUnitDisplayPanel.addSelectedTray(tray);
			}
		}
	}

	public void pickSelected() {
		Set<Tray> trays = StorageUnitDisplayPanel.getSelectedTrays();
		Set<Stock> stocks = Service.getValidPickDestinations(trays);
		this.dlgPickTrays.clearStocks();
		this.dlgPickTrays.setStocks(stocks);
		this.dlgPickTrays.setRequiresStock(true);
		for (Tray tray : trays) {
			State state = tray.getCurrentState();
			ProductType type = tray.getProductType();
			if (state != null && type != null) {
				this.dlgPickTrays.setRequiresStock(!type.isLastState(state));
				break;
			}
		}
		this.dlgPickTrays.setVisible(true);
		if (this.dlgPickTrays.getWasAccepted()) {
			Stock destination = this.dlgPickTrays.getStock();
			Date time = this.dlgPickTrays.getDate();
			try {
				Service.pickTrays(trays, destination, time);
				this.repaint();
				JOptionPane.showMessageDialog(
						this,
						"The selected trays were picked to "
								+ destination.getName(), "Picked",
						JOptionPane.INFORMATION_MESSAGE);
				StorageUnitDisplayPanel.clearSelection();
			}
			catch (OutOfStockSpaceException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			catch (InconsistencyException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			catch (StockNotCompatibleException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			catch (PickTooEarlyException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			catch (PickTooLateException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void removeSelected() {
		int result = JOptionPane
				.showConfirmDialog(
						this,
						"Are these trays waste (if so, click \"Yes\")? If this is a correction of a bookkeeping error, just click \"No\". ",
						"Confirm deletion", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			Service.wasteTrays(StorageUnitDisplayPanel.getSelectedTrays());
			this.repaint();
			JOptionPane
					.showMessageDialog(
							this,
							"The selected trays were marked as waste and removed from the storage. ",
							"Removed", JOptionPane.INFORMATION_MESSAGE);
			StorageUnitDisplayPanel.clearSelection();
		}
		else if (result == JOptionPane.NO_OPTION) {
			Service.deleteTrays(StorageUnitDisplayPanel.getSelectedTrays());
			this.repaint();
			JOptionPane.showMessageDialog(this,
					"The selected trays were removed from the system. ",
					"Removed", JOptionPane.INFORMATION_MESSAGE);
			StorageUnitDisplayPanel.clearSelection();
		}
	}

	@Override
	public void onTraySelectionChanged() {
		if (StorageUnitDisplayPanel.getSelectedTraysTotal() > 0) {
			this.btnClearSelection.setEnabled(true);
			this.btnPickSelected.setEnabled(true);
			this.btnRemoveSelected.setEnabled(true);
			this.lblSelection.setText("Trays Selected: "
					+ StorageUnitDisplayPanel.getSelectedTraysTotal());
		}
		else {
			this.btnClearSelection.setEnabled(false);
			this.btnPickSelected.setEnabled(false);
			this.btnRemoveSelected.setEnabled(false);
			this.lblSelection.setText("No Trays Selected ");
		}
	}

	private class Controller implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == StockOverviewPanel.this.btnClearSelection) {
				StorageUnitDisplayPanel.clearSelection();
			}
			else if (src == StockOverviewPanel.this.btnPickSelected) {
				StockOverviewPanel.this.pickSelected();
			}
			else if (src == StockOverviewPanel.this.btnRemoveSelected) {
				StockOverviewPanel.this.removeSelected();
			}
			else if (src == StockOverviewPanel.this.btnSelectAll) {
				StockOverviewPanel.this.selectAll();
			}
			else if (src == StockOverviewPanel.this.btnSelectUnit) {
				StockOverviewPanel.this.selectUnit();
			}
		}

	}

	@Override
	public void update(UpdateSubject repainter) {
		this.repaint();
	}
}
