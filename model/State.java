package model;

/**
 * @author Thomas Jansen Van Rensburg
 */

import java.util.Date;

public class State {
	private Date startTime;
	private Date endTime;
	private Tray tray;
	private SubProcess subProcess;

	/**
	 * 
	 * @param tray
	 */

	public State(Tray tray) {
		this(tray, null, null);
	}

	/**
	 * 
	 * @param tray
	 * @param startTime
	 */
	public State(Tray tray, Date startTime) {
		this(tray, startTime, null);
	}

	/**
	 * 
	 * @param tray
	 * @param startTime
	 * @param endTime
	 */
	public State(Tray tray, Date startTime, Date endTime) {
		setTray(tray);
		setStartTime(startTime);
		setEndTime(endTime);
	}

	/**
	 * 
	 * 
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * 
	 * @param startTime
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * 
	 * 
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * 
	 * @param endTime
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * 
	 * 
	 */
	public Tray getTray() {
		return tray;
	}

	/**
	 * 
	 * @param tray
	 */
	public void setTray(Tray tray) {
		this.tray = tray;
		if (tray != null) {
			tray.addState(this);
		}
	}

	/**
	 * 
	 * 
	 */
	public SubProcess getSubProcess() {
		return subProcess;
	}

	/**
	 * 
	 * @param subProcess
	 */
	public void setSubProcess(SubProcess subProcess) {
		this.subProcess = subProcess;
	}
}
