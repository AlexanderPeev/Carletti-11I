package dao;

import java.util.Iterator;
import java.util.Set;

import model.ProductType;
import model.State;
import model.Stock;
import model.StorageUnit;
import model.SubProcess;
import model.Tray;
import model.User;

public abstract class Dao {
	private static Dao instance;

	public static Dao getInstance() {
		if (Dao.instance == null) Dao.instance = new JpaDao();
		return Dao.instance;
	}

	public abstract Set<Stock> getStocks();

	public abstract Iterator<Stock> getStocksIterator();

	public abstract int getStocksTotal();

	public abstract void addStock(Stock stock);

	public abstract void removeStock(Stock stock);

	public abstract void updateStock(Stock stock);

	public abstract void addProductType(ProductType productType);

	public abstract void updateProductType(ProductType productType);

	public abstract void removeProductType(ProductType productType);

	public abstract Set<ProductType> getAllProductTypes();

	public abstract void addState(SubProcess subProcess, State state);

	public abstract void updateState(State state);

	public abstract void addSubProcess(ProductType productType,
			SubProcess subProcess);

	public abstract void updateSubProcess(SubProcess subProcess);

	public abstract void removeSubProcess(ProductType productType,
			SubProcess subProcess);

	public abstract Set<User> getUsers();

	public abstract void addUser(User user);

	public abstract void updateUser(User user);

	public abstract void removeUser(User user);

	public abstract void addTray(Tray tray);

	public abstract void updateTray(Tray tray);

	public abstract void removeTray(Tray tray);

	public abstract void createSomeObjects();

	public abstract void assignSubProcessToStock(Stock stock,
			SubProcess subProcess);

	public abstract void unsignSubProcessFromStock(Stock stock,
			SubProcess subProcess);

	public abstract void addStorageUnit(StorageUnit unit);

	public abstract void removeStorageUnit(StorageUnit unit);

}