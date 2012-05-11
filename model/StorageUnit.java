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
