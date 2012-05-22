package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * The Stock class represents a generic storage location. It could be a
 * production machine, a storage for semi-products, or for finished ones. Uses
 * the strategy pattern for the store behavior.
 * 
 * @author Alexander Peev
 * 
 */
@Entity(name = "stocks")
public class Stock implements Comparable<Stock> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "stock_id")
	private int id;
	@Enumerated(EnumType.STRING)
	@Column(name = "stock_type")
	private StockType type;
	@Column(name = "stock_capacity")
	private int capacity;
	@Column(name = "stock_max_trays_per_storage_unit")
	private int maxTraysPerStorageUnit;
	@Column(name = "stock_storage_units_per_row")
	private int storageUnitsPerRow;
	@Column(name = "stock_name")
	private String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "stock")
	private List<StorageUnit> storageUnits = new ArrayList<StorageUnit>();
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "stocks_to_sub_processes", joinColumns = { @JoinColumn(name = "sub_process_to_stock_sub_process") }, inverseJoinColumns = { @JoinColumn(name = "sub_process_to_stock_stock") })
	private Set<SubProcess> subProcesses = new HashSet<SubProcess>();
	@Transient
	private StockStoreBehavior storeBehavior = new DefaultStockStoreBehavior();

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Stock() {
		this("New Stock");
	}

	public Stock(String name) {
		this(name, StockType.SEMI);
	}

	public Stock(String name, StockType type) {
		this(name, type, 0);
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

	/**
	 * 
	 * @param type
	 *            If the value is null, the value StockType.SEMI is used.
	 */
	public void setType(StockType type) {
		if (type == null) this.type = StockType.SEMI;
		else this.type = type;
	}

	public int getCapacity() {
		return capacity;
	}

	/**
	 * 
	 * @param capacity
	 *            If the value is below zero, the absolute value is used.
	 */
	public void setCapacity(int capacity) {
		this.capacity = Math.abs(capacity);
	}

	public int getMaxTraysPerStorageUnit() {
		return maxTraysPerStorageUnit;
	}

	/**
	 * 
	 * @param maxTraysPerStorageUnit
	 *            If the value is below zero, the absolute value is used.
	 */
	public void setMaxTraysPerStorageUnit(int maxTraysPerStorageUnit) {
		this.maxTraysPerStorageUnit = Math.abs(maxTraysPerStorageUnit);
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

	/**
	 * 
	 * @param storageUnitsPerRow
	 *            If the value is below zero, the absolute value is used.
	 */
	public void setStorageUnitsPerRow(int storageUnitsPerRow) {
		this.storageUnitsPerRow = Math.abs(storageUnitsPerRow);
	}

	public List<StorageUnit> getStorageUnits() {
		return new ArrayList<StorageUnit>(storageUnits);
	}

	public Iterator<StorageUnit> getStorageUnitsIterator() {
		return storageUnits.iterator();
	}

	public StorageUnit getStorageUnitAt(int index) {
		if (index < 0 || index >= this.storageUnits.size()) return null;
		return storageUnits.get(index);
	}

	public int getStorageUnitsTotal() {
		return storageUnits.size();
	}

	/**
	 * The link is bidirectional and is maintained by both sides.
	 * 
	 * @param storageUnit
	 *            The storage unit to add to the stock.
	 */
	public void addStorageUnit(StorageUnit storageUnit) {
		if (!this.storageUnits.contains(storageUnit)) {
			this.storageUnits.add(storageUnit);
			storageUnit.setStock(this);
		}
	}

	/**
	 * The link is bidirectional and is maintained by both sides.
	 * 
	 * @param storageUnit
	 *            The storage unit to remove from the stock.
	 */
	public void removeStorageUnit(StorageUnit storageUnit) {
		if (this.storageUnits.contains(storageUnit)) {
			this.storageUnits.remove(storageUnit);
			storageUnit.setStock(null);
		}
	}

	public Set<SubProcess> getSubProcesses() {
		return new HashSet<SubProcess>(subProcesses);
	}

	public Iterator<SubProcess> getSubProcessesIterator() {
		return subProcesses.iterator();
	}

	public int getSubProcessesTotal() {
		return subProcesses.size();
	}

	/**
	 * The link is bidirectional and is maintained by both sides.
	 * 
	 * @param subProcess
	 *            The subprocess to add to the compatibility list.
	 */
	public void addSubProcess(SubProcess subProcess) {
		if (!this.subProcesses.contains(subProcess)) {
			this.subProcesses.add(subProcess);
			subProcess.addStock(this);
		}
	}

	/**
	 * The link is bidirectional and is maintained by both sides.
	 * 
	 * @param subProcess
	 *            The subprocess to remove from the compatibility list.
	 */
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
		if (trays == null) return;
		Iterator<Tray> i = trays.iterator();
		while (i.hasNext()) {
			Tray tray = i.next();
			if (tray.getStorageUnit() != null
					&& tray.getStorageUnit().getStock() == this) i.remove();
		}
		if (trays.size() < 1) return;
		if (!this.canFit(trays.size())) throw new OutOfStockSpaceException(
				"The desired amount of trays cannot fit in the Stock. ");
		if (this.storeBehavior == null) this.storeBehavior = new DefaultStockStoreBehavior();
		this.storeBehavior.storeTrays(trays, this);
		if (trays.size() > 0) throw new InconsistencyException(
				"Inconsistency detected! Not all trays were deposited at the destination. ");
	}

	/**
	 * Simple toString override.
	 * 
	 * @return The name of the stock.
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Compares by name in a case-insensitive manner.
	 * 
	 * @param other
	 *            must not be null.
	 * @return the comparison result.
	 */
	@Override
	public int compareTo(Stock other) {
		return getName().compareToIgnoreCase(other.getName());
	}
}
