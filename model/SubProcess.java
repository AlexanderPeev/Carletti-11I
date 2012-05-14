/**
 * @author Ricardas Risys
 */

package model;

import java.util.ArrayList;
import java.util.List;

public class SubProcess {
	private int minTime;
	private int idealTime;
	private int maxTime;
	private String name;
	private int order;
	private List<Stock> stocks = new ArrayList<Stock>();

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

	public List<Stock> getStocks() {
		return new ArrayList<Stock>(stocks);
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
}
