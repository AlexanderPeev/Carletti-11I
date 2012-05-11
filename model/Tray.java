package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tsvetomir Iliev
 * 
 */
public class Tray {
	private StorageUnit storageUnit;
	private List<State> states = new ArrayList<State>();
	private ProductType productType;
	private int slotNumber;

	public Tray() {
		this(null, null, 0);
	}

	public Tray(StorageUnit storageUnit) {
		this(storageUnit, null, 0);
	}

	public Tray(StorageUnit storageUnit, ProductType productType, int slotNumber) {
		setStorageUnit(storageUnit);
		setProductType(productType);

		State state = new State(this);
		addState(state);
		this.slotNumber = slotNumber;
	}

	public StorageUnit getStorageUnit() {
		return storageUnit;
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}

	public void setStorageUnit(StorageUnit storageUnit) {
		this.storageUnit = storageUnit;
		if (storageUnit != null) {
			this.storageUnit.addTray(this);
		}
	}

	public List<State> getStates() {
		return new ArrayList<State>(states);
	}

	public void addState(State state) {
		if (!this.states.contains(state)) {
			this.states.add(state);
			state.setTray(this);
		}
	}

	public void removeState(State state) {
		if (this.states.contains(state)) {
			this.states.remove(state);
			state.setTray(null);
		}
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
		if (productType != null) {
			this.productType.addTray(this);
		}
	}

	public State nextState(Stock stock) {
		return null;
	}
}
