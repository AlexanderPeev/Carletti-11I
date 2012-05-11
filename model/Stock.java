package model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Stock class represents a generic storage location. It could be a
 * production machine, a storage for semi-products, or for finished ones.
 * 
 * @author Alexander Peev
 * 
 */
public class Stock {
	private StockType type;
	private int capacity;
	private int maxTraysPerStorageUnit;
	private int storageUnitsPerRow;
	private String name;
	private List<StorageUnit> storageUnits = new ArrayList<StorageUnit>();
	private List<SubProcess> subProcesses = new ArrayList<SubProcess>();

	public Stock() {
		this("");
	}

	public Stock(String name) {
		this(name, StockType.SEMI, 0, 0);
	}

	public Stock(String name, StockType type) {
		this(name, type, 0, 0);
	}

	public Stock(String name, StockType type, int capacity) {
		this(name, type, capacity, 16);
	}

	public Stock(String name, StockType type, int capacity,
			int maxTraysPerStorageUnit) {
		this(name, type, capacity, maxTraysPerStorageUnit, 10);
	}

	public Stock(String name, StockType type, int capacity,
			int maxTraysPerStorageUnit, int storageUnitsPerRow) {
		setName(name);
		setType(type);
		setCapacity(capacity);
		setMaxTraysPerStorageUnit(maxTraysPerStorageUnit);
		setStorageUnitsPerRow(storageUnitsPerRow);
	}

	public StockType getType() {
		return type;
	}

	public void setType(StockType type) {
		this.type = type;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getMaxTraysPerStorageUnit() {
		return maxTraysPerStorageUnit;
	}

	public void setMaxTraysPerStorageUnit(int maxTraysPerStorageUnit) {
		this.maxTraysPerStorageUnit = maxTraysPerStorageUnit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStorageUnitsPerRow() {
		return this.storageUnitsPerRow;
	}

	public void setStorageUnitsPerRow(int storageUnitsPerRow) {
		this.storageUnitsPerRow = storageUnitsPerRow;
	}

	public List<StorageUnit> getStorageUnits() {
		return new ArrayList<StorageUnit>(storageUnits);
	}

	public void addStorageUnit(StorageUnit storageUnit) {
		if (!this.storageUnits.contains(storageUnit)) {
			this.storageUnits.add(storageUnit);
			storageUnit.setStock(this);
		}
	}

	public void removeStorageUnit(StorageUnit storageUnit) {
		if (this.storageUnits.contains(storageUnit)) {
			this.storageUnits.remove(storageUnit);
			storageUnit.setStock(null);
		}
	}

	public List<SubProcess> getSubProcesses() {
		return new ArrayList<SubProcess>(subProcesses);
	}

	public void addSubProcess(SubProcess subProcess) {
		if (!this.subProcesses.contains(subProcess)) {
			this.subProcesses.add(subProcess);
			subProcess.addStock(this);
		}
	}

	public void removeSubProcess(SubProcess subProcess) {
		if (this.subProcesses.contains(subProcess)) {
			this.subProcesses.remove(subProcess);
			subProcess.removeStock(this);
		}
	}

	/**
	 * Checks whether a certain amount of trays will fit in this Stock if an
	 * attempt to add them is made.
	 * 
	 * @param totalTrays
	 *            The total number of trays to check for.
	 * @return true if the trays will fit, false otherwise. If the parameter is
	 *         less then or equal to zero, it will always return true.
	 */
	public boolean canFit(int totalTrays) {
		int remaining = totalTrays;
		if (remaining <= 0) return true;
		for (StorageUnit unit : this.storageUnits) {
			remaining -= this.maxTraysPerStorageUnit - unit.getTrays().size();
			if (remaining <= 0) return true;
		}
		return false;
	}

	/**
	 * Attempts to store a number of trays in this Stock. If the trays cannot
	 * fit, it will throw an OutOfStockSpaceException with a generic message. If
	 * for some bizarre reason the storing process completes without storing all
	 * the trays, an InconsistencyException is thrown.
	 * 
	 * @param trays
	 *            A list of the trays to store. If the list is empty or null,
	 *            nothing happens.
	 * @throws OutOfStockSpaceException
	 *             if canFit(trays.size()) returns false
	 * @throws InconsistencyException
	 *             if the method completes without storing all the trays
	 */
	public void storeTrays(List<Tray> trays) throws OutOfStockSpaceException,
			InconsistencyException {
		if (trays == null || trays.size() < 1) return;
		if (!this.canFit(trays.size())) throw new OutOfStockSpaceException(
				"The desired amount of trays cannot fit in the Stock. ");
		for (StorageUnit unit : this.storageUnits) {
			for (int taken = unit.getTrays().size(); taken < this.maxTraysPerStorageUnit; taken++) {
				Tray tray = trays.remove(0);
				unit.addTray(tray);
				if (trays.size() < 1) return;
			}
		}
		throw new InconsistencyException(
				"Inconsistency detected! Not all trays were deposited at the destination. ");
	}

	@Override
	public String toString() {
		return getName();
	}
}
