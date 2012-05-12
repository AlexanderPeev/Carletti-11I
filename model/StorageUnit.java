package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tsvetomir Iliev
 * 
 */
public class StorageUnit {
	private Stock stock;
	private List<Tray> trays = new ArrayList<Tray>();
	private int slotNumber;

	public StorageUnit(Stock stock, int slotNumber) {
		setStock(stock);
		this.slotNumber = slotNumber;
	}

	public Stock getStock() {
		return stock;
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
		if (stock != null) {
			this.stock.addStorageUnit(this);
		}
	}

	public List<Tray> getTrays() {
		return new ArrayList<Tray>(trays);
	}

	public void addTray(Tray tray) {
		if (!this.trays.contains(tray)) {
			this.trays.add(tray);
			// TODO Tsvetomir - make an algorithm to set
			// the slot number (via tray.setSlotNumber)
			// to an appropriate number, based on the
			// existing trays and their numbers. Hint:
			// iterate over the trays (sorted after
			// their slot numbers) and set the number to
			// the first found hole in the sequence of
			// trays, and if no hole is found set it to
			// the largest found slot number + 1.
			tray.setStorageUnit(this);
		}
	}

	public void removeTray(Tray tray) {
		if (this.trays.contains(tray)) {
			this.trays.remove(tray);
			tray.setStorageUnit(null);
		}
	}
}
