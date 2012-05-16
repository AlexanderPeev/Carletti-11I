package service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import model.GroupType;
import model.InconsistencyException;
import model.OutOfStockSpaceException;
import model.ProductType;
import model.State;
import model.Stock;
import model.StockState;
import model.StockType;
import model.StorageUnit;
import model.SubProcess;
import model.Tray;
import model.User;
import dao.Dao;

public class Service {
	private static User currentUser = null;
	private static Dao dao = Dao.getInstance();

	/**
	 * Logs in the user or throws an exception with a generic message on
	 * failure.
	 * 
	 * @author Alexander Peev
	 * @param username
	 *            the supplied user name.
	 * @param password
	 *            the supplied password.
	 * @throws LoginException
	 *             If the login procedure fails.
	 */
	public static void login(String username, String password)
			throws LoginException {
		Set<User> users = dao.getUsers();
		for (User user : users) {
			if (username.equals(user.getUsername())) {
				if (password.equals(user.getPassword())) {
					Service.currentUser = user;
					user.applyPermissions();
					return;
				}
			}
		}
		throw new LoginException("Login failed, try again. ");
	}

	/**
	 * @author Alexander Peev
	 * @return The current logged in user. May be null.
	 */
	public static User getCurrentUser() {
		return Service.currentUser;
	}

	/**
	 * Sets the current logged in user.
	 * 
	 * @author Alexander Peev
	 * @param currentUser
	 *            The desired user
	 */

	public static void setCurrentUser(User currentUser) {
		Service.currentUser = currentUser;
	}

	/**
	 * Returns all the stocks in the data storage.
	 * 
	 * @author Alexander Peev
	 */

	public static Set<Stock> getAllStocks() {
		return dao.getStocks();
	}

	/**
	 * Creates a new stock object and adds it to the data storage. If the name
	 * is null or contains only trim-able whitespace, the name will be
	 * substituted by the string "New Stock". The rest of the parameters are
	 * left untouched, but are still subject to model constraints.
	 * 
	 * @author Alexander Peev
	 * @param name
	 *            If the value is null or contains only trim-able whitespace,
	 *            the name will be substituted by the string "New Stock".
	 */
	public static Stock createStock(String name, StockType type, int capacity,
			int maxTraysPerStorageUnit, int storageUnitsPerRow) {
		String sanitizedName = "";
		if (name == null || name.trim().length() < 1) sanitizedName = "New Stock";
		else sanitizedName = name.trim();
		Stock stock = new Stock(sanitizedName, type, capacity,
				maxTraysPerStorageUnit, storageUnitsPerRow);
		dao.addStock(stock);
		return stock;
	}

	/**
	 * Removes a stock from the data storage. The stock must have no trays
	 * stored inside it in order for the method to succeed. If the stock object
	 * is null, nothing will happen.
	 * 
	 * @author Alexander Peev
	 * @throws StockNotEmptyException
	 *             if the stock object has any trays.
	 */
	public static void removeStock(Stock stock) throws StockNotEmptyException {
		if (stock == null) return;
		for (StorageUnit unit : stock.getStorageUnits()) {
			for (Tray tray : unit.getTrays()) {
				if (tray != null) throw new StockNotEmptyException(
						"The stock is not empty. It must be empty in order to be removed from the system. ");
			}
		}
		dao.removeStock(stock);
	}

	/**
	 * Updates a stock with the data storage. If the stock object is null,
	 * nothing will happen. Model constraints are in effect during the update
	 * process.
	 * 
	 * @author Alexander Peev
	 * @param name
	 *            The stock name. Extra spaces are trimmed. If the value is
	 *            null, it is substituted by an empty string.
	 */
	public static void updateStock(Stock stock, String name, StockType type,
			int capacity, int maxTraysPerStorageUnit, int storageUnitsPerRow) {
		if (stock == null) return;
		stock.setName((name + "").trim());
		stock.setCapacity(capacity);
		stock.setMaxTraysPerStorageUnit(maxTraysPerStorageUnit);
		stock.setStorageUnitsPerRow(storageUnitsPerRow);
		stock.setType(type);
		dao.updateStock(stock);
	}

	/**
	 * Picks a number of trays and moves them to the next state into a new
	 * destination stock.
	 * 
	 * @author Alexander Peev
	 * @param trays
	 *            The list of trays to pick to the new destination.
	 * @param destination
	 *            The destination to pick to.
	 * @param time
	 *            The actual time of picking.
	 * @throws OutOfStockSpaceException
	 *             if the amount of trays cannot fit in the destination.
	 * @throws InconsistencyException
	 *             if an internal error was detected and not all trays were
	 *             deposited successfully.
	 * @throws StockNotCompatibleException
	 *             if the destination is not marked as compatible to the trays'
	 *             next sub process.
	 * @throws PickTooEarlyException
	 *             if some of the trays would be picked too early according to
	 *             the time parameter.
	 * @throws PickTooLateException
	 *             if some of the trays would be picked too late according to
	 *             the time parameter.
	 */
	public static void pickTrays(List<Tray> trays, Stock destination, Date time)
			throws OutOfStockSpaceException, InconsistencyException,
			StockNotCompatibleException, PickTooEarlyException,
			PickTooLateException {
		Date actualTime = time;
		if (actualTime == null) actualTime = new Date(
				System.currentTimeMillis());
		ProductType productType = null;
		State state = null;
		SubProcess subProcess = null;
		List<Tray> processed = new ArrayList<Tray>(trays);
		for (Tray tray : trays) {
			ProductType type = tray.getProductType();
			SubProcess sp = tray.getCurrentState().getSubProcess();
			if ((productType != null && productType != type)
					|| (subProcess != null && subProcess != sp)) {
				processed.remove(tray);
			}
			else {
				productType = type;
				subProcess = sp;
				state = tray.getCurrentState();
			}
		}
		try {
			trays.retainAll(processed);
		}
		catch (UnsupportedOperationException ex) {
			Set<Tray> removed = new HashSet<Tray>();
			for (Tray tray : trays) {
				if (!processed.contains(tray)) removed.add(tray);
			}
			for (Tray tray : removed) {
				trays.remove(tray);
			}
		}
		for (Tray tray : processed) {
			state = tray.getCurrentState();
			long end = actualTime.getTime();
			SubProcess sp = state.getSubProcess();
			int difference = (int) (Math.abs(end
					- state.getStartTime().getTime()) / 60000);
			if (sp.getMinTime() > difference) {
				throw new PickTooEarlyException(
						"One or more trays cannot be picked at the desired time - they would not be ready yet. ");
			}
			else if (sp.getMaxTime() <= difference) { throw new PickTooLateException(
					"One or more trays cannot be picked at the desired time - they would be already wasted. "); }
		}
		if (productType != null
				&& !productType.getNextSubProcess(state).getStocks()
						.contains(destination)) { throw new StockNotCompatibleException(
				"The destination stock is not compatible with the desired trays. "); }
		if (!destination.canFit(processed.size())) throw new OutOfStockSpaceException(
				"The desired amount of trays cannot fit in the destination. ");
		for (Tray tray : processed) {
			state = tray.getCurrentState();
			Service.updateState(state, tray, state.getStartTime(), actualTime);
			subProcess = tray.getProductType().getNextSubProcess(state);
			state = Service.createState(subProcess, tray, actualTime, null);
		}
		destination.storeTrays(processed);
		for (Tray tray : processed) {
			dao.updateTray(tray);
		}
	}

	/**
	 * Returns all the stocks which are running out of space
	 * 
	 * @author Alexander Peev
	 * @param percentFullAtLeast
	 *            The minimum percentage of occupied space required for a stock
	 *            to be considered running out of space - from 0.0 to 100.0
	 * @return A set of stocks which have too little free space.
	 */
	public static Set<Stock> getCriticalStocks(double percentFullAtLeast) {
		double threshold = Math.min(Math.abs(percentFullAtLeast), 100.0);
		Set<Stock> stocks = dao.getStocks();
		Set<Stock> full = new HashSet<Stock>();
		for (Stock stock : stocks) {
			double percentageFull = 0;
			int totalCapacity = stock.getCapacity()
					* stock.getMaxTraysPerStorageUnit(), totalStored = 0;
			for (StorageUnit unit : stock.getStorageUnits()) {
				totalStored += unit.getTrays().size();
			}
			percentageFull = (totalStored * 100.0 / totalCapacity);
			if (percentageFull >= threshold) {
				full.add(stock);
			}
		}
		return full;
	}

	/**
	 * Returns a set of stocks which are common compatible picking destinations
	 * for the provided list of trays.
	 * 
	 * @author Alexander Peev
	 * @param trays
	 *            The trays whose valid destinations to get.
	 * @return A set of stocks which is the intersection of all of the
	 *         individual trays' compatible stocks.
	 */
	public static Set<Stock> getValidPickDestinations(List<Tray> trays) {
		Set<Stock> stocks = null;
		if (trays != null) for (Tray tray : trays) {
			ProductType type = tray.getProductType();
			SubProcess sp = type.getNextSubProcess(tray.getCurrentState());
			if (stocks == null) {
				stocks = new HashSet<Stock>();
				stocks.addAll(sp.getStocks());
			}
			else {
				stocks.retainAll(sp.getStocks());
			}
		}
		if (stocks == null) {
			stocks = new HashSet<Stock>();
		}
		return stocks;
	}
	
	/**
	 * @author Thomas Van Rensburg
	 */
	public static ProductType createProductType(String name) {
		String sanitizedProductType = "";
		if (name == null || name.trim().length() < 1) sanitizedProductType = "New ProductType";
		else sanitizedProductType = name.trim();
		ProductType productType = new ProductType(sanitizedProductType);
		dao.addProductType(productType);
		return productType;

	}

	/**
	 * @author Thomas Van Rensburg
	 */
	public static void updateProductType(ProductType productType, String name) {
		if (productType == null) return;

		productType.setName(name);
		dao.updateProductType(productType);
	}

	/**
	 * @author Thomas Van Rensburg
	 */
	public static void deleteProductType(ProductType productType) {
		if (productType == null) return;

		productType.setName(null);
		dao.removeProductType(productType);
	}

	/**
	 * @author Thomas Van Rensburg
	 */
	public static Set<ProductType> getAllProductTypes() {
		return dao.getAllProductTypes();
	}

	/**
	 * @author Thomas Van Rensburg
	 */
	public static void moveSubProcessUp(SubProcess subProcess,
			ProductType productType) {// passing product type because one directional link in diagram
		List<SubProcess> subProcesses = productType.getSubProcesses();
		if(!subProcesses.contains(subProcess))return;
		int index = subProcesses.indexOf(subProcess), current = subProcess.getOrder();//stores order current subprocess
		if(index < 1)return;//check if first element
		
		SubProcess previous = subProcesses.get(index - 1);//get the previous element
		subProcess.setOrder(previous.getOrder());//swap the orders
		previous.setOrder(current);//swap the orders
		productType.reSortSubProcesses();//resort subprocesses to the new order after a movement
		
		dao.updateSubProcess(subProcess);//update dao for jpa purposes
		dao.updateSubProcess(previous);
	}

	/**
	 * @author Thomas Van Rensburg
	 */
	public static void moveSubProcessDown(SubProcess subProcess,
			ProductType productType) {
		List<SubProcess> subProcesses = productType.getSubProcesses();
		if(!subProcesses.contains(subProcess))return;
		int index = subProcesses.indexOf(subProcess), current = subProcess.getOrder();
		if(index < 0 || index >= subProcesses.size() - 1)return;
		
		SubProcess next = subProcesses.get(index + 1);
		subProcess.setOrder(next.getOrder());
		next.setOrder(current);
		productType.reSortSubProcesses();
		
		dao.updateSubProcess(subProcess);
		dao.updateSubProcess(next);
	}

	/**
	 * @author Thomas Van Rensburg
	 */
	public static State createState(SubProcess subProcess, Tray tray,
			Date startTime, Date endTime) {
		if (tray == null || subProcess == null) { return null; }

		State state = new State(tray, startTime, endTime);
		dao.addState(subProcess, state);
		return state;
	}

	/**
	 * @author Thomas Van Rensburg
	 */
	public static void updateState(State state, Tray tray, Date startTime,
			Date endTime) {
		if (state == null) return;

		state.setTray(tray);
		state.setStartTime(startTime);
		state.setEndTime(endTime);
		dao.updateState(state);
	}

	/**
	 * Creates a new sub process object and adds it to the data storage. If the
	 * name is null or contains only whitespace, the name will be substituted by
	 * the string "New Sub Process". All times are in minutes.
	 * 
	 * @author Ricardas Risys
	 * @param productType
	 *            used to assign to provided product type
	 * @param order
	 *            position of sub product for state determination
	 * @param name
	 * @param minTime
	 * @param idealTime
	 * @param maxTime
	 */
	public static SubProcess createSubProcess(ProductType productType,
			int pOrder, String name, int pMinTime, int pIdealTime, int pMaxTime) {

		if (productType == null) { return null; }
		int order = pOrder, minTime = pMinTime, idealTime = pIdealTime, maxTime = pMaxTime;
		if (order < 0) {
			order = 0;
		}

		String sanitizedName = "";
		if (name == null || name.trim().length() < 1) {
			sanitizedName = "New Sub Process";
		}
		else {
			sanitizedName = name.trim();
		}

		if (minTime < 0) {
			minTime = 0;
		}

		if (idealTime < 0) {
			idealTime = 0;
		}

		if (maxTime < 0) {
			maxTime = 0;
		}

		SubProcess subProcess = new SubProcess(order, sanitizedName, minTime,
				idealTime, maxTime);

		dao.addSubProcess(productType, subProcess);
		return subProcess;
	}

	/**
	 * Updates a sub process with the data storage. If the sub process object is
	 * null, nothing will happen. Model constraints are in effect during the
	 * update process. All times are in minutes.
	 * 
	 * @author Ricardas Risys
	 * @param subProcess
	 * @param order
	 * @param name
	 * @param minTime
	 * @param idealTime
	 * @param maxTime
	 */
	public static void updateSubProcess(SubProcess subProcess, int order,
			String name, int minTime, int idealTime, int maxTime) {

		if (subProcess == null) { return; }

		subProcess.setOrder(order);
		subProcess.setName((name + "").trim());
		subProcess.setMinTime(minTime);
		subProcess.setIdealTime(idealTime);
		subProcess.setMaxTime(maxTime);

		dao.updateSubProcess(subProcess);
	}

	/**
	 * Removes a sub process from the data storage. If the product type or sub
	 * process object is null, nothing will happen
	 * 
	 * @author Ricardas Risys
	 * @param productType
	 * @param subProcess
	 */
	public static void deleteSubProcess(ProductType productType,
			SubProcess subProcess) {
		if (productType == null || subProcess == null) { return; }

		dao.removeSubProcess(productType, subProcess);
	}

	/**
	 * Gets all assigned sub processes for stock object
	 * 
	 * @author Ricardas Risys
	 * @param stock
	 */
	public static Set<SubProcess> getStockSubProcesses(Stock stock) {
		return stock.getSubProcesses();
	}

	/**
	 * Gets all assigned stocks for sub process object
	 * 
	 * @author Ricardas Risys
	 * @param subProcess
	 */
	public static Set<Stock> getSubProcessStocks(SubProcess subProcess) {
		return subProcess.getStocks();
	}

	/**
	 * Creates a new user object and adds it to the data storage. If the
	 * username or password or group is null or contains only whitespace user
	 * will not be created
	 * 
	 * @author Ricardas Risys
	 * @param username
	 * @param password
	 * @param group
	 * @return user or null
	 */
	public static User createUser(String username, String password,
			GroupType group) {
		if (username == null || username.trim().length() < 1) { return null; }

		if (password == null || password.trim().length() < 1) { return null; }

		if (group == null) { return null; }

		User user = new User(username.trim(), password.trim(), group);
		return user;
	}

	/**
	 * Updates a user with the data storage. If the user or group object is
	 * null, nothing will happen. Model constraints are in effect during the
	 * update process.
	 * 
	 * @author Ricardas Risys
	 * @param user
	 * @param username
	 * @param password
	 * @param group
	 */
	public static void updateUser(User user, String username, String password,
			GroupType group) {

		if (user == null || group == null) { return; }

		user.setUsername((username + "").trim());
		user.setPassword((password + "").trim());
		user.setGroup(group);

		dao.updateUser(user);
	}

	/**
	 * Removes a user from the data storage. If the user object is null, nothing
	 * will happen
	 * 
	 * @param user
	 */
	public static void deleteUser(User user) {
		if (user == null) { return; }

		dao.removeUser(user);
	}

	/**
	 * @author Ricardas Risys
	 * @param stock
	 */
	public static StockState getWorstState(Stock stock) {
		// TODO
		return null;
	}
}
