package service;

import java.util.Set;

import javax.security.auth.login.LoginException;

import model.Stock;
import model.StockType;
import model.StorageUnit;
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
}
