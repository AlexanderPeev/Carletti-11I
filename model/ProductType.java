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
import javax.persistence.OneToMany;

/**
 * 
 * @author Thomas Jansen Van Rensburg
 * 
 */
@Entity(name = "product_types")
public class ProductType implements Comparable<ProductType> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private int id;
	@Column(name = "product_type_name")
	private String name;
	@OneToMany(cascade = CascadeType.ALL)
	private List<SubProcess> subProcesses = new ArrayList<SubProcess>();
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "productType")
	private List<Tray> trays = new ArrayList<Tray>();

	public ProductType() {

	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProductType(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SubProcess> getSubProcesses() {
		reSortSubProcesses();
		return new ArrayList<SubProcess>(subProcesses);
	}

	/**
	 * Re-sort subProcesses by their order field
	 */
	public void reSortSubProcesses() {
		Collections.sort(this.subProcesses);
	}

	public void addSubProcess(SubProcess subProcess) {
		if (!this.subProcesses.contains(subProcess)) {
			this.subProcesses.add(subProcess);
			reSortSubProcesses();
		}
	}

	public void removeSubProcess(SubProcess subProcess) {
		if (this.subProcesses.contains(subProcess)) {
			this.subProcesses.remove(subProcess);
		}
	}

	public List<Tray> getTrays() {
		return new ArrayList<Tray>(trays);
	}

	public void addTray(Tray tray) {
		if (!this.trays.contains(tray)) {
			this.trays.add(tray);
			tray.setProductType(this);
		}
	}

	/**
	 * Add tray to storage unit Link is maintained from both productType and
	 * tray
	 * 
	 * @param tray
	 */
	public void removeTray(Tray tray) {
		if (this.trays.contains(tray)) {
			this.trays.remove(tray);
			tray.setProductType(null);
		}
	}

	public boolean isLastState(State state) {
		if (state == null) return false;
		reSortSubProcesses();
		SubProcess sp = state.getSubProcess();
		if (sp == null || !this.subProcesses.contains(sp)) return false;

		if (this.subProcesses.indexOf(sp) == this.subProcesses.size() - 1) return true;
		return false;
	}

	public SubProcess getNextSubProcess(State state) {
		if (state == null) return null;
		reSortSubProcesses();
		SubProcess sp = state.getSubProcess();
		if (sp == null
				|| !this.subProcesses.contains(sp)
				|| this.subProcesses.indexOf(sp) >= this.subProcesses.size() - 1) return null;
		return this.subProcesses.get(this.subProcesses.indexOf(sp) + 1);
	}

	@Override
	public String toString() {
		return name + "";
	}

	@Override
	public int compareTo(ProductType arg0) {
		return this.getName().compareToIgnoreCase(arg0.getName());
	}

}
