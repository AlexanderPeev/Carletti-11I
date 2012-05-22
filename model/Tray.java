package model;

import java.util.ArrayList;
import java.util.Date;
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

	public Tray(ProductType type) {
		this(null, type, 0);
	}

	public Tray(StorageUnit storageUnit) {
		this(storageUnit, null, 0);
	}

	public Tray(StorageUnit storageUnit, ProductType productType, int slotNumber) {
		this.slotNumber = slotNumber;
		setStorageUnit(storageUnit);
		setProductType(productType);

		State state = new State(this, new Date(System.currentTimeMillis()));
		state.setSubProcess(productType.getSubProcesses().get(0));
		addState(state);
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
		if (this.storageUnit == storageUnit) return;
		if (this.storageUnit != null) {
			this.storageUnit.removeTray(this);
		}
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
			if (state != null) state.setTray(this);
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
		if (this.productType == productType) return;
		if (this.productType != null) {
			this.productType.removeTray(this);
		}
		this.productType = productType;
		if (productType != null) {
			this.productType.addTray(this);
		}
	}

	public State getCurrentState() {
		return this.states.get(this.states.size() - 1);
	}
}
