package model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Thomas Jansen Van Rensburg
 */
@Entity(name = "states")
public class State {
	public static final State wasted = new State();
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "state_id")
	private int id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "state_start_time")
	private Date startTime;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "state_end_time", nullable = true)
	private Date endTime;
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "state_tray")
	private Tray tray;
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, optional = true)
	@JoinColumn(name = "state_sub_process", nullable = true)
	private SubProcess subProcess;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Empty constructor for JPA purposes
	 */
	public State() {
	}

	public State(Tray tray) {
		this(tray, null, null);
	}

	public State(Tray tray, Date startTime) {
		this(tray, startTime, null);
	}

	/**
	 * Creates a state of production that a tray passes through before
	 * completion.
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
