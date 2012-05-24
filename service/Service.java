package service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
		for (int i = 0; i < stock.getCapacity(); i++) {
			StorageUnit unit = new StorageUnit(stock, i);
			stock.addStorageUnit(unit);
		}
		dao.addStock(stock);
		for (StorageUnit unit : stock.getStorageUnits()) {
			dao.addStorageUnit(unit);
		}
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
	public static void deleteStock(Stock stock) throws StockNotEmptyException {
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
	 * @throws StockNotEmptyException
	 */
	public static void updateStock(Stock stock, String name, StockType type,
			int capacity, int maxTraysPerStorageUnit, int storageUnitsPerRow)
			throws StockNotEmptyException {
		if (stock == null) return;
		stock.setName((name + "").trim());
		if (capacity > stock.getCapacity()) {
			for (int i = stock.getCapacity(); i <= capacity; i++) {
				StorageUnit unit = new StorageUnit(stock, i - 1);
				stock.addStorageUnit(unit);
				dao.addStorageUnit(unit);
			}
		}
		else if (capacity < stock.getCapacity()) {
			for (int last = stock.getStorageUnitsTotal() - 1; last >= capacity; last--) {
				StorageUnit unit = stock.getStorageUnitAt(last);
				for (Tray tray : unit.getTrays()) {
					if (tray != null) throw new StockNotEmptyException(
							"One of the storage units is not empty. It must be empty in order to be removed from the system. ");
				}
			}
			while (capacity < stock.getStorageUnitsTotal()) {
				StorageUnit unit = stock.getStorageUnitAt(stock
						.getStorageUnitsTotal() - 1);
				stock.removeStorageUnit(unit);
				dao.removeStorageUnit(unit);
			}
		}
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
	public static void pickTrays(Collection<Tray> trays, Stock destination,
			Date time) throws OutOfStockSpaceException, InconsistencyException,
			StockNotCompatibleException, PickTooEarlyException,
			PickTooLateException {
		Date actualTime = time;
		if (actualTime == null) actualTime = new Date(
				System.currentTimeMillis());
		ProductType productType = null;
		State state = null;
		SubProcess subProcess = null;
		if (trays == null || trays.size() < 1) return;
		List<Tray> processed = new ArrayList<Tray>(trays);
		for (Tray tray : trays) {
			ProductType type = tray.getProductType();
			State cs = tray.getCurrentState();
			if (cs == null) continue;
			SubProcess sp = cs.getSubProcess();
			if ((productType != null && productType != type)
					|| (subProcess != null && subProcess != sp)) {
				processed.remove(tray);
			}
			else {
				productType = type;
				subProcess = sp;
				state = cs;
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
			if (state == null) continue;
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
		/**
		 * Check if it is the last state - then we will have to remove the trays
		 * from storage rather than move them to another storage.
		 */
		if (productType != null && productType.isLastState(state)) {
			for (Tray tray : processed) {
				state = tray.getCurrentState();
				Service.updateState(state, tray, state.getStartTime(),
						actualTime);
				tray.setStorageUnit(null);
				dao.updateTray(tray);
			}
			return;
		}
		else if (destination == null) return;

		if (productType != null
				&& !productType.getNextSubProcess(state).getStocks()
						.contains(destination)) { throw new StockNotCompatibleException(
				"The destination stock is not compatible with the desired trays. "); }
		if (!destination.canFit(processed.size())) throw new OutOfStockSpaceException(
				"The desired amount of trays cannot fit in the destination. ");
		for (Tray tray : processed) {
			state = tray.getCurrentState();
			if (state != null) {
				Service.updateState(state, tray, state.getStartTime(),
						actualTime);
				subProcess = tray.getProductType().getNextSubProcess(state);
			}
			else subProcess = tray.getProductType().getSubProcesses().get(0);
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
	public static Set<Stock> getValidPickDestinations(Collection<Tray> trays) {
		Set<Stock> stocks = null;
		if (trays != null) for (Tray tray : trays) {
			ProductType type = tray.getProductType();
			SubProcess sp = type.getNextSubProcess(tray.getCurrentState());
			if (sp != null) {
				if (stocks == null) {
					stocks = new HashSet<Stock>();
					stocks.addAll(sp.getStocks());
				}
				else {
					stocks.retainAll(sp.getStocks());
				}
			}
		}
		if (stocks == null) {
			stocks = new HashSet<Stock>();
		}
		return stocks;
	}

	/**
	 * Creates a product type eg. Skumbananer for which subprocesses and trays
	 * can be created.
	 * 
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
	 * @param productType
	 * @param name
	 */
	public static void updateProductType(ProductType productType, String name) {
		if (productType == null) return;

		productType.setName(name);
		dao.updateProductType(productType);
	}

	/**
	 * Removes a product type. If product type is in production, a
	 * ProductTypeInProductionException will be thrown.
	 * 
	 * @author Thomas Van Rensburg
	 * @throws ProductTypeInProductionException
	 */
	public static void deleteProductType(ProductType productType)
			throws ProductTypeInProductionException {
		if (productType == null) return;
		for (Tray tray : productType.getTrays()) {
			if (tray.getStorageUnit() != null) throw new ProductTypeInProductionException(
					"You cannot remove the product type, because it is currently in production. ");
		}
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
	 * Changes the subprocess order. Swaps the current subprocess associated
	 * with a product type with the previous subprocess.
	 * 
	 * @author Thomas Van Rensburg
	 */
	public static void moveSubProcessUp(SubProcess subProcess,
			ProductType productType) {// passing product type because one
		// directional link in diagram
		List<SubProcess> subProcesses = productType.getSubProcesses();
		if (!subProcesses.contains(subProcess)) return;
		int index = subProcesses.indexOf(subProcess);// stores order current
														// subprocess
		int current = subProcess.getOrder();
		if (index < 1) return;// check if first element

		SubProcess previous = subProcesses.get(index - 1);// get the previous
		// element
		subProcess.setOrder(previous.getOrder());// swap the orders
		previous.setOrder(current);// swap the orders
		productType.reSortSubProcesses();// resort subprocesses to the new order
		// after a movement

		dao.updateSubProcess(subProcess);// update dao for jpa purposes
		dao.updateSubProcess(previous);
	}

	/**
	 * Changes the subprocess order. Swaps the current subprocess associated
	 * with a product type with the next subprocess.
	 * 
	 * @author Thomas Van Rensburg
	 */
	public static void moveSubProcessDown(SubProcess subProcess,
			ProductType productType) {
		List<SubProcess> subProcesses = productType.getSubProcesses();
		if (!subProcesses.contains(subProcess)) return;
		int index = subProcesses.indexOf(subProcess);
		int current = subProcess.getOrder();
		if (index < 0 || index >= subProcesses.size() - 1) return;

		SubProcess next = subProcesses.get(index + 1);
		subProcess.setOrder(next.getOrder());
		next.setOrder(current);
		productType.reSortSubProcesses();

		dao.updateSubProcess(subProcess);
		dao.updateSubProcess(next);
	}

	/**
	 * Creates the state of a tray. The state is the representation of a tray at
	 * a given time.
	 * 
	 * @author Thomas Van Rensburg
	 */
	public static State createState(SubProcess subProcess, Tray tray,
			Date startTime, Date endTime) {
		if (tray == null || subProcess == null) { return null; }

		if (startTime == null) { return null; }

		if (endTime != null && startTime.getTime() >= endTime.getTime()) { return null; }

		if (startTime.getTime() < 0
				|| (endTime != null && endTime.getTime() < 0)) { return null; }

		State state = new State(tray, startTime, endTime);
		dao.addState(subProcess, state);
		return state;
	}

	/**
	 * Updates the state of a tray at a given time.
	 * 
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
	 * the string "New Sub Process". All times are in minutes. If data is
	 * invalid SubProcessException will be raised
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
	 * @throws SubProcessException
	 */
	public static SubProcess createSubProcess(ProductType productType,
			int order, String name, int minTime, int idealTime, int maxTime)
			throws SubProcessException {

		if (productType == null) { throw new SubProcessException(
				"Product Type is not provided."); }

		if (order < 0) {
			order = 0;
		}

		for (SubProcess subProcess : productType.getSubProcesses()) {
			if (subProcess.getOrder() == order) { throw new SubProcessException(
					"Sub process " + subProcess.getName()
							+ " has the same order number."); }
		}

		String sanitizedName = "";
		if (name == null || name.trim().length() < 1) {
			sanitizedName = "New Sub Process";
		}
		else {
			sanitizedName = name.trim();
		}

		Service.validateSubProcessTimes(minTime, idealTime, maxTime);

		SubProcess subProcess = new SubProcess(order, sanitizedName, minTime,
				idealTime, maxTime);

		dao.addSubProcess(productType, subProcess);
		return subProcess;
	}

	/**
	 * Updates a sub process with the data storage. Model constraints are in
	 * effect during the update process. All times are in minutes. If data is
	 * invalid SubProcessException will be raised
	 * 
	 * @author Ricardas Risys
	 */
	public static void updateSubProcess(SubProcess subProcess, int order,
			String name, int minTime, int idealTime, int maxTime)
			throws SubProcessException {

		if (subProcess == null) { throw new SubProcessException(
				"Sub process is not provided."); }

		if (name == null || name.trim().length() < 1) { throw new SubProcessException(
				"Name cannot be empty."); }

		Service.validateSubProcessTimes(minTime, idealTime, maxTime);

		if (order < 0) {
			order = 0;
		}

		subProcess.setOrder(order);
		subProcess.setName((name).trim());
		subProcess.setMinTime(minTime);
		subProcess.setIdealTime(idealTime);
		subProcess.setMaxTime(maxTime);

		dao.updateSubProcess(subProcess);
	}

	/**
	 * Validates times for sub process create / update process and throws an
	 * SubProcessException if data is invalid
	 * 
	 * @author Ricardas Risys
	 */
	public static void validateSubProcessTimes(int minTime, int idealTime,
			int maxTime) throws SubProcessException {
		if (minTime < 0 || idealTime < 0 || maxTime < 0) { throw new SubProcessException(
				"Times cannot be less then 0."); }

		if (minTime == idealTime || minTime == maxTime || idealTime == maxTime) { throw new SubProcessException(
				"Times cannot be equal."); }

		if (minTime > idealTime || minTime > maxTime || idealTime > maxTime) { throw new SubProcessException(
				"Min time must be lower than Ideal time. Ideal time must be lower then Max time."); }
	}

	/**
	 * Removes a sub process from the data storage. If the product type or sub
	 * process object is null, nothing will happen
	 * 
	 * @author Ricardas Risys
	 */
	public static void deleteSubProcess(ProductType productType,
			SubProcess subProcess) throws SubProcessException {
		if (productType == null || subProcess == null) { return; }
		for (Tray tray : productType.getTrays()) {
			if (tray != null && tray.getCurrentState() != null
					&& tray.getCurrentState().getSubProcess() == subProcess) { throw new SubProcessException(
					"You cannot remove this sub-process, because some trays have a state with it. "); }
		}
		dao.removeSubProcess(productType, subProcess);
		List<SubProcess> sps = productType.getSubProcesses();
		for (SubProcess sp : sps) {
			sp.setOrder(sps.indexOf(sp));
			dao.updateSubProcess(sp);
		}
	}

	/**
	 * Gets all assigned sub processes for stock object
	 * 
	 * @author Ricardas Risys
	 */
	public static Set<SubProcess> getStockSubProcesses(Stock stock) {
		return stock.getSubProcesses();
	}

	/**
	 * Gets all assigned stocks for sub process object
	 * 
	 * @author Ricardas Risys
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
	 * @return user or null
	 */
	public static User createUser(String username, String password,
			GroupType group) {
		if (username == null || username.trim().length() < 1) { return null; }

		if (password == null || password.trim().length() < 1) { return null; }

		if (group == null) { return null; }

		User user = new User(username.trim(), password.trim(), group);
		dao.addUser(user);
		return user;
	}

	/**
	 * Updates a user with the data storage. If the user or group object is
	 * null, nothing will happen. Model constraints are in effect during the
	 * update process.
	 * 
	 * @author Ricardas Risys
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
	 * Checks all trays inside storage units for specified Stock item and picks
	 * the worst state
	 * 
	 * @author Ricardas Risys
	 * @param stock
	 */
	public static StockState getWorstState(Stock stock) {
		StockState stockState = StockState.EARLY;

		for (StorageUnit storageUnit : stock.getStorageUnits()) {
			for (Tray tray : storageUnit.getTrays()) {
				State state = tray.getCurrentState();
				SubProcess subProcess = state.getSubProcess();

				long start = state.getStartTime().getTime();
				long current = System.currentTimeMillis();
				long diff = current - start;
				long minTime = subProcess.getMinTime() * 60 * 1000;
				long idealTime = subProcess.getIdealTime() * 60 * 1000;
				long maxTime = subProcess.getMaxTime() * 60 * 1000;

				if (minTime <= diff && diff < idealTime) {
					if (!stockState.equals(StockState.MINIMUM_OPTIMAL)
							&& !stockState.equals(StockState.OPTIMAL_MAXIMUM)) {
						stockState = StockState.MINIMUM_OPTIMAL;
					}
				}
				else if (idealTime <= diff && diff < maxTime) {
					if (!stockState.equals(StockState.OPTIMAL_MAXIMUM)) {
						stockState = StockState.OPTIMAL_MAXIMUM;
					}
				}
				else if (diff >= maxTime) { return StockState.WASTE; }
			}
		}

		return stockState;
	}

	/**
	 * Assigns subprocess to stock
	 * 
	 * @author Ricardas Risys
	 * @param stock
	 * @param subProcess
	 */
	public static void assignSubProcessToStock(Stock stock,
			SubProcess subProcess) {
		dao.assignSubProcessToStock(stock, subProcess);
	}

	/**
	 * Removes subprocess from stock
	 * 
	 * @author Ricardas Risys
	 * @param stock
	 * @param subProcess
	 */
	public static void unsignSubProcessFromStock(Stock stock,
			SubProcess subProcess) {
		dao.unsignSubProcessFromStock(stock, subProcess);
	}

	/**
	 * String to Date utility function.
	 * 
	 * @author Alexander Peev
	 */
	public static Date stringToDate(String date) {
		GregorianCalendar cal = new GregorianCalendar();
		List<Integer> values = new ArrayList<Integer>();
		StringTokenizer sr = new StringTokenizer(date, " -_:;,/\\\t\n\r\f");

		while (sr.hasMoreTokens()) {
			String token = sr.nextToken();
			if (token == null || token.trim().length() < 1) continue;
			try {
				values.add(Math.abs(Integer.parseInt(token)));
			}
			catch (Throwable e) {
				values.add(0);
			}
		}
		if (values.size() > 0) cal.set(GregorianCalendar.YEAR, values.get(0));
		if (values.size() > 1) cal.set(GregorianCalendar.MONTH, values.get(1));
		if (values.size() > 2) cal.set(GregorianCalendar.DAY_OF_MONTH,
				values.get(2));
		if (values.size() > 3) cal.set(GregorianCalendar.HOUR_OF_DAY,
				values.get(3));
		else cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		if (values.size() > 4) cal.set(GregorianCalendar.MINUTE, values.get(4));
		else cal.set(GregorianCalendar.MINUTE, 0);
		if (values.size() > 5) cal.set(GregorianCalendar.SECOND, values.get(5));
		else cal.set(GregorianCalendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * Date to String utility function.
	 * 
	 * @author Alexander Peev
	 */
	public static String dateToString(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		if (date != null) cal.setTime(date);
		String year = cal.get(GregorianCalendar.YEAR) + "", month = cal
				.get(GregorianCalendar.MONTH) + "", day = cal
				.get(GregorianCalendar.DAY_OF_MONTH) + "", hour = cal
				.get(GregorianCalendar.HOUR_OF_DAY) + "", minute = cal
				.get(GregorianCalendar.MINUTE) + "", second = cal
				.get(GregorianCalendar.SECOND) + "";
		while (year.length() < 4)
			year = "0" + year;
		while (month.length() < 2)
			month = "0" + month;
		while (day.length() < 2)
			day = "0" + day;
		while (hour.length() < 2)
			hour = "0" + hour;
		while (minute.length() < 2)
			minute = "0" + minute;
		while (second.length() < 2)
			second = "0" + second;
		return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":"
				+ second;
	}

	/**
	 * Here we create a tray with the attributes "productType" which describes
	 * the product in the system, the stock from where we can see and choose the
	 * next destination of the tray and amount where we specify how much trays
	 * we want to create. In this method we have 2 exceptions
	 * "InconsistencyExeception" notifies us when program can't manage to add
	 * the trays to the stock by some reason. "OutofStockSpaceExceptions"
	 * notifies us when trays can't fit into the stock.
	 * 
	 * @author Tsvetomir Iliev
	 */
	public static Set<Tray> createTrays(ProductType productType, Stock stock,
			int amount) throws OutOfStockSpaceException, InconsistencyException {
		Set<Tray> trays = new HashSet<Tray>();
		List<Tray> store = new ArrayList<Tray>();
		if (stock == null || productType == null) return null;
		if (!stock.canFit(amount)) {
			int total = 0;
			Iterator<StorageUnit> i = stock.getStorageUnitsIterator();
			while (i.hasNext()) {
				StorageUnit unit = i.next();
				total += stock.getMaxTraysPerStorageUnit()
						- unit.getTrays().size();
			}
			throw new OutOfStockSpaceException(
					"The trays will not fit in the desired stock. The stock has only "
							+ total + " free place" + (total == 1 ? "" : "s")
							+ " for trays. ");
		}

		for (int i = 0; i < amount; i++) {
			Tray tray = new Tray(productType);
			store.clear();
			store.add(tray);
			stock.storeTrays(store);
			dao.addTray(tray);
			trays.add(tray);
		}
		return trays;

	}

	/**
	 * With "deleteTrays" we actually delete the trays in the collection passwd
	 * as a parameter.
	 * 
	 * @author Tsvetomir Iliev
	 */
	public static void deleteTrays(Collection<Tray> trays) {
		if (trays == null) return;
		for (Tray tray : trays) {
			deleteTray(tray);
		}
	}

	/**
	 * With "deleteTray" we remove a tray from the system.
	 * 
	 * @author Tsvetomir Iliev
	 */
	public static void deleteTray(Tray tray) {
		if (tray == null) return;
		tray.setStorageUnit(null);
		dao.removeTray(tray);
	}

	/**
	 * The method “getExpiringTrays“ monitors and notifies us if any trays are
	 * about to expire. In other words expired trays may be avoided, thus
	 * helping us to minimize waste. start is the time when the new state of the
	 * trays begins. "maxTime" is the maximum time that the tray can have before
	 * expiring,in other words if the "maxTime" is reached then the tray is
	 * expired. "idealTime" means the ideal time of picking the trays "diff" is
	 * the difference of time between the start and the current time and if the
	 * difference is more than the "idealTime" and less than the "maxTime" then
	 * we notify that the trays are going to expire.
	 * 
	 * @author Tsvetomir Iliev
	 */
	public static Set<Tray> getExpiringTrays() {
		Set<Tray> expiring = new HashSet<Tray>();
		for (Stock stock : dao.getStocks()) {
			for (StorageUnit storageUnit : stock.getStorageUnits()) {
				for (Tray tray : storageUnit.getTrays()) {
					State state = tray.getCurrentState();
					if (state == null) continue;
					SubProcess subProcess = state.getSubProcess();

					long start = state.getStartTime().getTime();
					long current = System.currentTimeMillis();
					long diff = current - start;
					long idealTime = subProcess.getIdealTime() * 60 * 1000;
					long maxTime = subProcess.getMaxTime() * 60 * 1000;
					if (idealTime <= diff && diff < maxTime) {
						expiring.add(tray);
					}
				}
			}
		}
		return expiring;
	}

	/**
	 * 
	 * Here "getWastedTrays" notifies us of trays which are already expire in
	 * other words wasted. "start" is the time when the new state of the trays
	 * begins. "idealTime" means the ideal time of picking the trays "diff" is
	 * the difference of time between the start and the current time and if the
	 * difference is more than than the "maxTime" then the trays are wasted.
	 * 
	 * @author Tsvetomir Iliev
	 */
	public static Set<Tray> getWastedTrays() {
		Set<Tray> wasted = new HashSet<Tray>();
		for (Stock stock : dao.getStocks()) {
			for (StorageUnit storageUnit : stock.getStorageUnits()) {
				for (Tray tray : storageUnit.getTrays()) {
					State state = tray.getCurrentState();
					if (state == null) continue;
					SubProcess subProcess = state.getSubProcess();

					long start = state.getStartTime().getTime();
					long current = System.currentTimeMillis();
					long diff = current - start;
					long maxTime = subProcess.getMaxTime() * 60 * 1000;
					if (diff >= maxTime) {
						wasted.add(tray);
					}
				}
			}
		}
		return wasted;
	}

	/**
	 * Marks a number of trays as waste and removes them from their stock while
	 * still keeping them in the system.
	 * 
	 * @author Alexander Peev
	 * @param trays
	 */
	public static void wasteTrays(Collection<Tray> trays) {
		if (trays == null) return;
		for (Tray tray : trays) {
			wasteTray(tray);
		}
	}

	/**
	 * Marks a tray as waste and removes it from its stock while still keeping
	 * it in the system.
	 * 
	 * @author Alexander Peev
	 * @param tray
	 */
	private static void wasteTray(Tray tray) {
		if (tray == null) return;
		tray.setStorageUnit(null);
		tray.addState(State.wasted);
	}

}
