package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

/**
 * 
 * @author Tsvetomir Iliev
 * 
 */
@Entity(name = "storage_units")
public class StorageUnit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "storage_unit_id")
	private int id;
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "storage_unit_stock")
	private Stock stock;
	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, mappedBy = "storageUnit")
	private List<Tray> trays = new ArrayList<Tray>();
	@Column(name = "storage_unit_order")
	@OrderColumn(name = "storage_unit_order", insertable = true, updatable = true, nullable = false)
	private int order;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StorageUnit() {
	}

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

	/**
	 * Link is bidirectional and maintained from both sides
	 */
	public void setStock(Stock stock) {
		if (stock == this.stock) return;
		if (this.stock != null) {
			this.stock.removeStorageUnit(this);
		}
		this.stock = stock;
		if (this.stock != null) {
			this.stock.addStorageUnit(this);
		}
	}

	public List<Tray> getTrays() {
		return new ArrayList<Tray>(trays);
	}

	/**
	 * Link is bidirectional and maintained from both sides
	 */
	public void addTray(Tray tray) {
		if (!this.trays.contains(tray)) {
			this.setSlotNumber(tray);
			this.trays.add(tray);
			tray.setStorageUnit(this);
		}
	}

	/**
	 * "setSlotNumber" is a method that creates a number for the slot where the
	 * tray is going to be put.
	 */
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
