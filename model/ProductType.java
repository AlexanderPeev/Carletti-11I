package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Thomas Jansen Van Rensburg
 * 
 */
public class ProductType implements Comparable<ProductType> {
	private String name;
	private List<SubProcess> subProcesses = new ArrayList<SubProcess>();
	private List<Tray> trays = new ArrayList<Tray>();

	/**
	 * 
	 * @param name
	 */
	public ProductType(String name) {
		setName(name);
	}

	/**
	 * 
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * 
	 */
	public List<SubProcess> getSubProcesses() {
		return new ArrayList<SubProcess>(subProcesses);
	}

	public void reSortSubProcesses() {
		Collections.sort(this.subProcesses);
	}

	/**
	 * 
	 * @param subProcess
	 */
	public void addSubProcess(SubProcess subProcess) {
		if (!this.subProcesses.contains(subProcess)) {
			this.subProcesses.add(subProcess);
			reSortSubProcesses();
		}
	}

	/**
	 * 
	 * @param subProcess
	 */
	public void removeSubProcess(SubProcess subProcess) {
		if (this.subProcesses.contains(subProcess)) {
			this.subProcesses.remove(subProcess);
		}
	}

	/**
	 * 
	 * 
	 */
	public List<Tray> getTrays() {
		return new ArrayList<Tray>(trays);
	}

	/**
	 * 
	 * @param tray
	 */
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
		SubProcess sp = state.getSubProcess();
		if (sp == null || !this.subProcesses.contains(sp)) return false;

		if (this.subProcesses.indexOf(sp) == this.subProcesses.size() - 1) return true;
		return false;
	}

	public SubProcess getNextSubProcess(State state) {
		if (state == null) return null;
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
