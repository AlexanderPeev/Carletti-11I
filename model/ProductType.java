package model;

import java.util.ArrayList;
import java.util.List;

public class ProductType {
	private String name;
	private List<SubProcess> subProcesses = new ArrayList<SubProcess>();
	private List<Tray> trays = new ArrayList<Tray>();

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
		return new ArrayList<SubProcess>(subProcesses);
	}

	public void addSubProcess(SubProcess subProcess) {
		if (!this.subProcesses.contains(subProcess)) {
			this.subProcesses.add(subProcess);
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

	public void removeTray(Tray tray) {
		if (this.trays.contains(tray)) {
			this.trays.remove(tray);
			tray.setProductType(null);
		}
	}
}
