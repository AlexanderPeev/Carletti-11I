package model;

import java.util.Date;

public class State {
	private Date startTime;
	private Date endTime;
	private Tray tray;
	private SubProcess subProcess;

	public State(Tray tray) {
		this(tray, null, null);
	}

	public State(Tray tray, Date startTime) {
		this(tray, startTime, null);
	}

	public State(Tray tray, Date startTime, Date endTime) {
		setTray(tray);
		setStartTime(startTime);
		setEndTime(endTime);
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Tray getTray() {
		return tray;
	}

	public void setTray(Tray tray) {
		this.tray = tray;
		if (tray != null) {
			tray.addState(this);
		}
	}

	public SubProcess getSubProcess() {
		return subProcess;
	}

	public void setSubProcess(SubProcess subProcess) {
		this.subProcess = subProcess;
	}
}
