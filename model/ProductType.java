package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Thomas Jansen Van Rensburg
 * 
 */
public class ProductType {
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

	/**
	 * 
	 * @param subProcess
	 */
	public void addSubProcess(SubProcess subProcess) {
		if (!this.subProcesses.contains(subProcess)) {
			this.subProcesses.add(subProcess);
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
}
