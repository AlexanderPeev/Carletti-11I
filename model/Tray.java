package model;

import java.util.ArrayList;
import java.util.List;

public class Tray {
	private StorageUnit storageUnit;
	private List<State> states = new ArrayList<State>();
	private ProductType productType;

	public Tray() {
		this(null, null);
	}

	public Tray(StorageUnit storageUnit) {
		this(storageUnit, null);
	}

	public Tray(StorageUnit storageUnit, ProductType productType) {
		setStorageUnit(storageUnit);
		setProductType(productType);

		State state = new State(this);
		addState(state);
	}

	public StorageUnit getStorageUnit() {
		return storageUnit;
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
