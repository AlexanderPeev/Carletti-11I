package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.Stock;
import model.StorageUnit;
import model.Tray;

/**
 * A panel for displaying the contents of a Stock object. It provides a visual
 * representation of all storage units, and means to select and pick trays.
 * 
 * @author Alexander Peev
 * 
 */
public class StockOverviewPanel extends JPanel implements ClickListener,
		TraySelectionChangedListener {

	private DashboardPanel owner = null;
	private JPanel pnlStorageUnits, pnlZoom;
	private JButton btnClearSelection, btnPickSelected, btnRemoveSelected,
			btnSelectAll;
	private Stock stock;
	private List<StorageUnitDisplayPanel> compactDisplays;
	private StorageUnitDisplayPanel selectedCompactDisplay = null;
	private StorageUnitDisplayPanel zoomDisplay = new StorageUnitDisplayPanel(
			null, DisplayMode.FULL);
	private Controller controller = new Controller();
	private Repainter repainter;

	public StockOverviewPanel(DashboardPanel owner) {
		this.owner = owner;
		setLayout(new BorderLayout(0, 0));
		StorageUnitDisplayPanel.addTraySelectionChangedListener(this);

		this.compactDisplays = new ArrayList<StorageUnitDisplayPanel>();
		this.repainter = new Repainter(this);

		JPanel pnlEast = new JPanel(), pnlButtons = new JPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
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
		btnSelectAll = new JButton("Select All");
		this.btnSelectAll.setPreferredSize(new Dimension(200, 20));
		this.btnSelectAll.addActionListener(controller);
		pnlButtons.add(btnSelectAll);
		btnClearSelection = new JButton("Clear Selection");
		this.btnClearSelection.setEnabled(false);
		this.btnClearSelection.setPreferredSize(new Dimension(200, 20));
		this.btnClearSelection.addActionListener(controller);
		pnlButtons.add(btnClearSelection);

		pnlButtons.setPreferredSize(new Dimension(210, 105));

		pnlStorageUnits = new JPanel();
		pnlStorageUnits.setLayout(new GridLayout(1, 0, 0, 0));

		JScrollPane scpMain = new JScrollPane(pnlStorageUnits);
		add(scpMain, BorderLayout.CENTER);
	}

	public Repainter getRepainter() {
		return this.repainter;
	}

	public void updateDisplay() {
		this.pnlStorageUnits.removeAll();
		if (this.stock != null) {
			int columns = this.stock.getStorageUnitsPerRow();
			System.out.println(columns);
			this.pnlStorageUnits.setLayout(new GridLayout(0, columns, 5, 5));
			StorageUnitDisplayPanel.clearSelection();
			for (StorageUnitDisplayPanel display : this.compactDisplays) {
				display.detatch();
				display.removeClickListener(this);
			}
			this.compactDisplays.clear();
			for (StorageUnit storageUnit : this.stock.getStorageUnits()) {
				StorageUnitDisplayPanel display = new StorageUnitDisplayPanel(
						storageUnit, DisplayMode.COMPACT);
				this.compactDisplays.add(display);
				this.pnlStorageUnits.add(display);
				display.addClickListener(this);
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
			this.zoomDisplay = new StorageUnitDisplayPanel(unit,
					DisplayMode.FULL);
			display.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3,
					true));
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

	@Override
	public void onTraySelectionChanged() {
		if (StorageUnitDisplayPanel.getSelectedTraysTotal() > 0) {
			this.btnClearSelection.setEnabled(true);
			this.btnPickSelected.setEnabled(true);
			this.btnRemoveSelected.setEnabled(true);
		}
		else {
			this.btnClearSelection.setEnabled(false);
			this.btnPickSelected.setEnabled(false);
			this.btnRemoveSelected.setEnabled(false);
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
				// TODO
			}
			else if (src == StockOverviewPanel.this.btnRemoveSelected) {
				// TODO
			}
			else if (src == StockOverviewPanel.this.btnSelectAll) {
				StockOverviewPanel.this.selectAll();
			}
		}

	}
}
