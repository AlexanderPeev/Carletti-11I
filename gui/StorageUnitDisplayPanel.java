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
import model.StorageUnit;
import model.SubProcess;
import model.Tray;

/**
 * Provides a graphical interface to a StorageUnit and its contained trays.
 * 
 * @author Alexander Peev
 * 
 */
public class StorageUnitDisplayPanel extends JPanel {
	private Set<Tray> selectedTrays;
	private StorageUnit storageUnit;
	private Controller controller = new Controller();
	private DisplayMode displayMode;

	public StorageUnitDisplayPanel(StorageUnit storageUnit, DisplayMode mode) {
		this.storageUnit = storageUnit;
		this.displayMode = mode;
		this.selectedTrays = new HashSet<Tray>();
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

	public Set<Tray> getSelectedTrays() {
		return new HashSet<Tray>(this.selectedTrays);
	}

	public Iterator<Tray> getSelectedTraysIterator() {
		return this.selectedTrays.iterator();
	}

	public int getSelectedTraysTotal() {
		return this.selectedTrays.size();
	}

	public void addSelectedTray(Tray tray) {
		if (tray == null) return;
		if (this.selectedTrays.add(tray)) this.repaint();
	}

	public void removeSelectedTray(Tray tray) {
		if (tray == null) return;
		if (this.selectedTrays.remove(tray)) this.repaint();
	}

	public void clearSelection() {
		this.selectedTrays.clear();
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.storageUnit != null) {
			int width = this.getWidth(), height = this.getHeight(), max = storageUnit
					.getStock().getMaxTraysPerStorageUnit();
			g.setFont(new Font("Arial", Font.PLAIN, (int) (height / max * .6)));
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
					List<State> states = tray.getStates();
					State state = null;
					if (states.size() > 0) state = states
							.get(states.size() - 1);
					String text = "";
					if (state == null) {
						g.setColor(Color.CYAN);
						text = j + ": Unknown state - " + productType.getName();
					}
					else {
						long end = System.currentTimeMillis();
						if (state.getEndTime() != null) end = state
								.getEndTime().getTime();
						SubProcess subProcess = state.getSubProcess();
						text = j + ": " + subProcess.getName() + " - "
								+ productType.getName();
						int difference = (int) (Math.abs(end
								- state.getStartTime().getTime()) / 60000);
						if (subProcess.getMinTime() > difference) {
							g.setColor(Color.BLUE);
						}
						else if (subProcess.getIdealTime() > difference) {
							g.setColor(Color.GREEN);
						}
						else if (subProcess.getMaxTime() > difference) {
							g.setColor(Color.YELLOW);
						}
						else {
							g.setColor(Color.RED);
						}
					}
					g.fillRoundRect((int) (width * .05), (i * height / max)
							+ (height / max / 10), (int) (width * .9),
							(int) (height / max * .8),
							Math.min(width / 10, height / max / 3), height
									/ max / 3);
					if (selectedTrays.contains(tray)) {
						g.setColor(Color.DARK_GRAY);
						g.drawRoundRect((int) (width * .05), (i * height / max)
								+ (height / max / 10), (int) (width * .9),
								(int) (height / max * .8),
								Math.min(width / 10, height / max / 3), height
										/ max / 3);
						g.drawRoundRect((int) (width * .05) - 1,
								(i * height / max) + (height / max / 10) - 1,
								(int) (width * .9) + 2,
								(int) (height / max * .8) + 2,
								Math.min(width / 10, height / max / 3) + 1,
								height / max / 3 + 1);
					}
					if (this.displayMode == DisplayMode.FULL) {
						g.setFont(StorageUnitDisplayPanel.getAppropriateFont(
								text, (int) (width * .75),
								(int) (height / max * .6)));
						g.setColor(Color.BLACK);
						g.drawString(text, width / 8, (i * height / max)
								+ (int) (height / max * .7));
					}
				}
				else {
					g.setColor(Color.WHITE);
					g.fillRoundRect((int) (width * .05), (i * height / max)
							+ (height / max / 10), (int) (width * .9),
							(int) (height / max * .8),
							Math.min(width / 10, height / max / 3), height
									/ max / 3);
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
			if (StorageUnitDisplayPanel.this.displayMode != DisplayMode.FULL) return;
			int y = arg0.getY();
			if (StorageUnitDisplayPanel.this.storageUnit != null) {
				int height = StorageUnitDisplayPanel.this.getHeight(), max = storageUnit
						.getStock().getMaxTraysPerStorageUnit();
				List<Tray> trays = storageUnit.getTrays();
				HashMap<Integer, Tray> traysMap = new HashMap<Integer, Tray>();
				for (Tray tray : trays) {
					traysMap.put(tray.getSlotNumber(), tray);
				}
				int j = y * max / height;
				int i = max - 1 - j;

				if (traysMap.containsKey(i)) {
					Tray tray = traysMap.get(i);
					if (StorageUnitDisplayPanel.this.selectedTrays
							.contains(tray)) StorageUnitDisplayPanel.this.selectedTrays
							.remove(tray);
					else StorageUnitDisplayPanel.this.selectedTrays.add(tray);
					StorageUnitDisplayPanel.this.repaint();
				}

			}
		}
	}
}