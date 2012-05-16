package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Tsvetomir Iliev
 * 
 */
public class StorageUnit {
	private Stock stock;
	private List<Tray> trays = new ArrayList<Tray>();
	private int order;

	public StorageUnit(Stock stock, int order) {
		setStock(stock);
		this.order = order;
	}

	public Stock getStock() {
		return stock;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
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
			this.setSlotNumber(tray);
			this.trays.add(tray);
			tray.setStorageUnit(this);
		}
	}

	// In the method setSlotnumber we put slotNumber to -1 so later to be able
	// to
	// check for any holes in the tray,we declare a list as well where later
	// we put the numbers in it.With collections.sort we sort the numbers from
	// lowest to highest
	// After that we go through all num and check for holes.

	public void setSlotNumber(Tray tray) {
		int slotNumber = -1;
		List<Integer> numbers = new ArrayList<Integer>();
		for (Tray t : this.trays) {
			numbers.add(t.getSlotNumber());
		}
		Collections.sort(numbers);
		for (Integer num : numbers) {
			if (num > slotNumber + 1) {
				tray.setSlotNumber(slotNumber + 1);
				return;
			}
			slotNumber = num;
		}
		tray.setSlotNumber(slotNumber + 1);
	}

	public void removeTray(Tray tray) {
		if (this.trays.contains(tray)) {
			this.trays.remove(tray);
			tray.setStorageUnit(null);
		}
	}
}
