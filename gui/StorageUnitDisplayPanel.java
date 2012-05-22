package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import model.ProductType;
import model.State;
import model.StockState;
import model.StorageUnit;
import model.SubProcess;
import model.Tray;

/**
 * Provides a graphical interface to a StorageUnit and its contained trays. Uses
 * a modified version of the observer pattern.
 * 
 * @author Alexander Peev
 * 
 */
public class StorageUnitDisplayPanel extends JPanel implements
		TraySelectionChangedListener, ClickProvider {
	private static Set<Tray> selectedTrays = new HashSet<Tray>();
	private static Set<TraySelectionChangedListener> traySelectionChangedListeners = new HashSet<TraySelectionChangedListener>();
	private StorageUnit storageUnit;
	private Controller controller = new Controller();
	private DisplayMode displayMode;
	private Set<ClickListener> clickListeners = new HashSet<ClickListener>();

	public StorageUnitDisplayPanel(StorageUnit storageUnit, DisplayMode mode) {
		StorageUnitDisplayPanel.addTraySelectionChangedListener(this);
		this.storageUnit = storageUnit;
		this.displayMode = mode;
		this.addMouseListener(controller);
	}

	public DisplayMode getDisplayMode() {
		return this.displayMode;
	}

	public void setDisplayMode(DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	public StorageUnit getStorageUnit() {
		return this.storageUnit;
	}

	public void setStorageUnit(StorageUnit storageUnit) {
		if (this.storageUnit != storageUnit) {
			this.storageUnit = storageUnit;
			clearSelection();
		}
	}

	public static Set<Tray> getSelectedTrays() {
		return new HashSet<Tray>(StorageUnitDisplayPanel.selectedTrays);
	}

	public static Iterator<Tray> getSelectedTraysIterator() {
		return StorageUnitDisplayPanel.selectedTrays.iterator();
	}

	public static int getSelectedTraysTotal() {
		return StorageUnitDisplayPanel.selectedTrays.size();
	}

	public static boolean isSelectable(Tray tray) {
		Set<ProductType> productTypes = new HashSet<ProductType>();
		Set<SubProcess> subProcesses = new HashSet<SubProcess>();
		for (Tray selected : StorageUnitDisplayPanel.selectedTrays) {
			if (productTypes.size() > 0 && subProcesses.size() > 0) break;
			ProductType productType = selected.getProductType();
			if (productType != null) productTypes.add(productType);
			State state = selected.getCurrentState();
			SubProcess subProcess = state.getSubProcess();
			if (subProcess != null) subProcesses.add(subProcess);
		}
		State state = tray.getCurrentState();
		SubProcess subProcess = null;
		if (state != null) subProcess = state.getSubProcess();
		if (productTypes.size() > 0
				&& !productTypes.contains(tray.getProductType())) return false;
		if (subProcesses.size() > 0 && !subProcesses.contains(subProcess)) return false;
		return true;
	}

	public static void addSelectedTray(Tray tray) {
		if (tray == null
				|| StorageUnitDisplayPanel.selectedTrays.contains(tray)) return;
		if (!StorageUnitDisplayPanel.isSelectable(tray)) return;
		if (StorageUnitDisplayPanel.selectedTrays.add(tray)) StorageUnitDisplayPanel
				.triggerTraySelectionChanged();
	}

	public static void removeSelectedTray(Tray tray) {
		if (tray == null) return;
		if (StorageUnitDisplayPanel.selectedTrays.remove(tray)) StorageUnitDisplayPanel
				.triggerTraySelectionChanged();
	}

	public static boolean hasSelectedTray(Tray tray) {
		if (tray == null) return false;
		return StorageUnitDisplayPanel.selectedTrays.contains(tray);
	}

	public static void clearSelection() {
		StorageUnitDisplayPanel.selectedTrays.clear();
		StorageUnitDisplayPanel.triggerTraySelectionChanged();
	}

	public static void triggerTraySelectionChanged() {
		for (TraySelectionChangedListener listener : StorageUnitDisplayPanel.traySelectionChangedListeners) {
			listener.onTraySelectionChanged();
		}
	}

	public void detatch() {
		StorageUnitDisplayPanel.removeTraySelectionChangedListener(this);
	}

	public static Set<TraySelectionChangedListener> getTraySelectionChangedListeners() {
		return new HashSet<TraySelectionChangedListener>(
				StorageUnitDisplayPanel.traySelectionChangedListeners);
	}

	public static Iterator<TraySelectionChangedListener> getTraySelectionChangedListenersIterator() {
		return StorageUnitDisplayPanel.traySelectionChangedListeners.iterator();
	}

	public static int getTraySelectionChangedListenersTotal() {
		return StorageUnitDisplayPanel.traySelectionChangedListeners.size();
	}

	public static void addTraySelectionChangedListener(
			TraySelectionChangedListener listener) {
		if (listener != null) StorageUnitDisplayPanel.traySelectionChangedListeners
				.add(listener);
	}

	public static void removeTraySelectionChangedListener(
			TraySelectionChangedListener listener) {
		if (listener != null) StorageUnitDisplayPanel.traySelectionChangedListeners
				.remove(listener);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.storageUnit != null) {
			int width = this.getWidth(), height = this.getHeight(), max = storageUnit
					.getStock().getMaxTraysPerStorageUnit(), unitFieldHeight = height
					/ max, unitHeight = (int) Math.round(unitFieldHeight * .8), unitWidth = (int) Math
					.round(width * .95), unitLeftOffset = (int) Math
					.round(width * .025), unitTopOffset = (unitFieldHeight / 10), textTopOffset = 0, textLeftOffset = (int) Math
					.round(unitLeftOffset + unitWidth * .05), textHeight = (int) Math
					.round(unitHeight * .9), textWidth = (int) Math
					.round(unitWidth * .9), textBaselineOffset = (int) Math
					.round(textHeight * .8);
			g.setFont(new Font("Arial", Font.PLAIN,
					(int) (unitFieldHeight * .6)));
			Color textColor = Color.BLACK, fillColor = Color.MAGENTA;
			List<Tray> trays = storageUnit.getTrays();
			HashMap<Integer, Tray> traysMap = new HashMap<Integer, Tray>();
			for (Tray tray : trays) {
				traysMap.put(tray.getSlotNumber(), tray);
			}
			for (int j = 0; j < max; j++) {
				int i = max - 1 - j;
				if (traysMap.containsKey(j)) {
					Tray tray = traysMap.get(j);
					ProductType productType = tray.getProductType();
					State state = tray.getCurrentState();
					String text = "";
					boolean displayTextColor = false;
					if (state == null) {
						fillColor = Color.CYAN;
						textColor = Color.BLACK;
						text = j + ": Unknown state - " + productType.getName();
					}
					else {
						long end = System.currentTimeMillis();
						if (state.getEndTime() != null) end = state
								.getEndTime().getTime();
						SubProcess subProcess = state.getSubProcess();
						text = j + ": " + subProcess.getName() + " - "
								+ productType.getName();
						int difference = (int) ((end - state.getStartTime()
								.getTime()) / 60000);
						if (subProcess.getMinTime() > difference) {
							fillColor = StockState.getColor(StockState.EARLY);
						}
						else if (subProcess.getIdealTime() > difference) {
							fillColor = StockState
									.getColor(StockState.MINIMUM_OPTIMAL);
						}
						else if (subProcess.getMaxTime() > difference) {
							fillColor = StockState
									.getColor(StockState.OPTIMAL_MAXIMUM);
						}
						else {
							fillColor = StockState.getColor(StockState.WASTE);
						}
						if (StorageUnitDisplayPanel.getSelectedTraysTotal() > 0
								&& !isSelectable(tray)) {
							textColor = fillColor;
							if (this.displayMode == DisplayMode.FULL) fillColor = Color.BLACK;
							else fillColor = Color.GRAY;
							displayTextColor = true;
						}
						else textColor = Color.BLACK;
					}
					g.setColor(fillColor);
					g.fillRoundRect((int) (unitLeftOffset),
							(i * unitFieldHeight) + unitTopOffset, unitWidth,
							unitHeight,
							Math.min(width / 10, unitFieldHeight / 3),
							unitFieldHeight / 3);
					if (selectedTrays.contains(tray)) {
						if (this.displayMode == DisplayMode.COMPACT) g
								.setColor(Color.CYAN);
						else g.setColor(Color.BLACK);
						g.drawRoundRect(unitLeftOffset, (i * unitFieldHeight)
								+ unitTopOffset, unitWidth, unitHeight,
								Math.min(width / 10, unitFieldHeight / 3),
								unitFieldHeight / 3);
						g.drawRoundRect(unitLeftOffset - 1,
								(i * unitFieldHeight) + unitTopOffset - 1,
								unitWidth + 2, unitHeight + 2,
								Math.min(width / 10, unitFieldHeight / 3) + 1,
								unitFieldHeight / 3 + 1);
					}
					if (this.displayMode == DisplayMode.FULL) {
						g.setColor(textColor);
						g.setFont(StorageUnitDisplayPanel.getAppropriateFont(
								text, textWidth, textHeight));
						g.drawString(text, textLeftOffset,
								(i * unitFieldHeight) + unitTopOffset
										+ textTopOffset + textBaselineOffset);
					}
					else if (displayTextColor) {
						g.setColor(textColor);
						g.fillRoundRect(unitLeftOffset, (i * unitFieldHeight)
								+ unitTopOffset, textLeftOffset,
								unitHeight - 1,
								Math.min(width / 10, unitFieldHeight / 3),
								unitFieldHeight / 3);
						g.setColor(Color.BLACK);
						g.drawRoundRect(unitLeftOffset, (i * unitFieldHeight)
								+ unitTopOffset, textLeftOffset,
								unitHeight - 1,
								Math.min(width / 10, unitFieldHeight / 3),
								unitFieldHeight / 3);
					}
				}
				else {
					g.setColor(Color.WHITE);
					g.fillRoundRect(unitLeftOffset, (i * unitFieldHeight)
							+ unitTopOffset, unitWidth, unitHeight,
							Math.min(width / 10, unitFieldHeight / 3),
							unitFieldHeight / 3);
				}
			}
		}
	}

	private static Font getAppropriateFont(String text, int width, int height) {
		int i = height;
		Font font = new Font("Arial", Font.PLAIN, i);
		FontRenderContext context = new FontRenderContext(null, false, false);
		Rectangle2D bounds = font.getStringBounds(text, context);
		while (i > 1
				&& (bounds.getWidth() > width || bounds.getHeight() > height)) {
			--i;
			font = new Font("Arial", Font.PLAIN, i);
			bounds = font.getStringBounds(text, context);
		}
		return font;
	}

	private class Controller implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// unused
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// unused

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// unused

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// unused

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (StorageUnitDisplayPanel.this.displayMode != DisplayMode.FULL) {
				StorageUnitDisplayPanel.this.fireClick();
				return;
			}
			int y = arg0.getY();
			if (StorageUnitDisplayPanel.this.storageUnit != null) {
				int height = StorageUnitDisplayPanel.this.getHeight(), max = storageUnit
						.getStock().getMaxTraysPerStorageUnit();
				List<Tray> trays = storageUnit.getTrays();
				HashMap<Integer, Tray> traysMap = new HashMap<Integer, Tray>();
				for (Tray tray : trays) {
					traysMap.put(tray.getSlotNumber(), tray);
				}
				int j = (int) ((double) y / (height / max));
				int i = max - 1 - j;
				if (traysMap.containsKey(i)) {
					Tray tray = traysMap.get(i);
					if (StorageUnitDisplayPanel.hasSelectedTray(tray)) StorageUnitDisplayPanel
							.removeSelectedTray(tray);
					else StorageUnitDisplayPanel.addSelectedTray(tray);
				}

			}
		}
	}

	@Override
	public void onTraySelectionChanged() {
		this.repaint();
	}

	@Override
	public void addClickListener(ClickListener listener) {
		if (listener != null) this.clickListeners.add(listener);
	}

	@Override
	public void removeClickListener(ClickListener listener) {
		if (listener != null) this.clickListeners.remove(listener);
	}

	@Override
	public Set<ClickListener> getClickListeners() {
		return new HashSet<ClickListener>(this.clickListeners);
	}

	@Override
	public Iterator<ClickListener> getClickListenersIterator() {
		return this.clickListeners.iterator();
	}

	@Override
	public int getClickListenersTotal() {
		return this.clickListeners.size();
	}

	@Override
	public void fireClick() {
		for (ClickListener listener : this.clickListeners) {
			if (listener != null) listener.onClick(this);
		}
	}
}
