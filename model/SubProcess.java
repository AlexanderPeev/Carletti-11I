package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

/**
 * @author Ricardas Risys
 */
@Entity(name = "sub_processes")
public class SubProcess implements Comparable<SubProcess> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sub_process_id")
	private int id;
	@Column(name = "sub_process_min_time")
	private int minTime;
	@Column(name = "sub_process_ideal_time")
	private int idealTime;
	@Column(name = "sub_process_max_time")
	private int maxTime;
	@Column(name = "sub_process_name")
	private String name;
	@Column(name = "sub_process_order")
	@OrderColumn(name = "sub_process_order", insertable = true, updatable = true, nullable = false)
	private int order;
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "sub_processes_to_stocks", joinColumns = { @JoinColumn(name = "sub_process_to_stock_stock") }, inverseJoinColumns = { @JoinColumn(name = "sub_process_to_stock_sub_process") })
	private Set<Stock> stocks = new HashSet<Stock>();

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SubProcess() {
		this(0, "", 0, 0, 0);
	}

	public SubProcess(int order) {
		this(order, "", 0, 0, 0);
	}

	public SubProcess(int order, String name) {
		this(order, name, 0, 0, 0);
	}

	public SubProcess(int order, String name, int minTime) {
		this(order, name, minTime, 0, 0);
	}

	public SubProcess(int order, String name, int minTime, int idealTime) {
		this(order, name, minTime, idealTime, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param order
	 * @param name
	 * @param minTime
	 * @param idealTime
	 * @param maxTime
	 */
	public SubProcess(int order, String name, int minTime, int idealTime,
			int maxTime) {
		setOrder(order);
		setName(name);
		setMinTime(minTime);
		setIdealTime(idealTime);
		setMaxTime(maxTime);
	}

	public int getMinTime() {
		return minTime;
	}

	public void setMinTime(int minTime) {
		this.minTime = minTime;
	}

	public int getIdealTime() {
		return idealTime;
	}

	public void setIdealTime(int idealTime) {
		this.idealTime = idealTime;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Set<Stock> getStocks() {
		return new HashSet<Stock>(stocks);
	}

	/**
	 * Link is bidirectional and maintained from both sides
	 * 
	 * @param stock
	 */
	public void addStock(Stock stock) {
		if (!this.stocks.contains(stock)) {
			this.stocks.add(stock);
			stock.addSubProcess(this);
		}
	}

	/**
	 * Link is bidirectional and maintained from both sides
	 * 
	 * @param stock
	 */
	public void removeStock(Stock stock) {
		if (this.stocks.contains(stock)) {
			this.stocks.remove(stock);
			stock.removeSubProcess(this);
		}
	}

	@Override
	public int compareTo(SubProcess arg0) {
		return this.order - arg0.getOrder();
	}

	@Override
	public String toString() {
		return (getOrder() + 1) + ": " + getName();
	}
}
