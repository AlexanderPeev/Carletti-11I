package model;

import java.util.ArrayList;
import java.util.List;

public class Stock {
	private StockType type;
	private int capacity;
	private int maxTraysPerStorageUnit;
	private String name;
	private List<StorageUnit> storageUnits = new ArrayList<StorageUnit>();
	private List<SubProcess> subProcesses = new ArrayList<SubProcess>();

	public Stock() {
		this("", null, 0, 0);
	}

	public Stock(String name) {
		this(name, null, 0, 0);
	}

	public Stock(String name, StockType type) {
		this(name, type, 0, 0);
	}

	public Stock(String name, StockType type, int capacity) {
		this(name, type, capacity, 0);
	}

	public Stock(String name, StockType type, int capacity,
			int maxTraysPerStorageUnit) {
		setName(name);
		setType(type);
		setCapacity(capacity);
		setMaxTraysPerStorageUnit(maxTraysPerStorageUnit);
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

	public Boolean canFit(int totalTrays) {
		return Boolean.FALSE;
	}

	public void storeTrays(List<Tray> trays) {
		//
	}

	@Override
	public String toString() {
		return getName();
	}
}
