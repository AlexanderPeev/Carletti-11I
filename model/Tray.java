package model;

import java.util.ArrayList;
import java.util.Date;
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
@Entity(name = "trays")
public class Tray {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tray_id")
	private int id;
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, optional = true)
	@JoinColumn(name = "tray_storage_unit")
	private StorageUnit storageUnit;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "tray")
	private List<State> states = new ArrayList<State>();
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "tray_product_type")
	private ProductType productType;
	@Column(name = "tray_slot_number")
	@OrderColumn(name = "tray_slot_number", insertable = true, updatable = true)
	private int slotNumber;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Tray() {
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
		if (this.storageUnit != null) {
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
		if (this.states.size() < 1) return null;
		return this.states.get(this.states.size() - 1);
	}
}
