package dao;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import model.GroupType;
import model.ProductType;
import model.State;
import model.Stock;
import model.StockType;
import model.StorageUnit;
import model.SubProcess;
import model.Tray;
import model.User;

public class Dao {
	private static Dao instance;
	private Set<Stock> stocks = new HashSet<Stock>();
	private Set<ProductType> productTypes = new HashSet<ProductType>();
	private Set<User> users = new HashSet<User>();

	public static Dao getInstance() {
		if (Dao.instance == null) Dao.instance = new Dao();
		return Dao.instance;
	}

	private Dao() {
		createSomeObjects();
	}

	public Set<Stock> getStocks() {
		return new HashSet<Stock>(this.stocks);
	}

	public Iterator<Stock> getStocksIterator() {
		return this.stocks.iterator();
	}

	public int getStocksTotal() {
		return this.stocks.size();
	}

	public void addStock(Stock stock) {
		this.stocks.add(stock);
	}

	public void removeStock(Stock stock) {
		this.stocks.remove(stock);
	}

	public void updateStock(Stock stock) {
		// unused until JPA

	}

	public void updateTray(Tray tray) {
		// unused until JPA

	}

	public void addProductType(ProductType productType) {
		productTypes.add(productType);
	}

	public void updateProductType(ProductType productType) {

		// unused until JPA
	}

	public void removeProductType(ProductType productType) {
		if (productType != null && productTypes.contains(productType)) productTypes
				.remove(productType);

	}

	public Set<ProductType> getAllProductTypes() {
		return new HashSet<ProductType>(productTypes);// return a copy and not
														// the original
	}

	public void addState(SubProcess subProcess, State state) {
		state.setSubProcess(subProcess);
	}

	public void updateState(State state) {
		// unused until JPA

	}

	public void addSubProcess(ProductType productType, SubProcess subProcess) {
		productType.addSubProcess(subProcess);
	}

	public void updateSubProcess(SubProcess subProcess) {
		// unused until JPA
	}

	public void removeSubProcess(ProductType productType, SubProcess subProcess) {
		productType.removeSubProcess(subProcess);
	}

	public Set<User> getUsers() {
		return new HashSet<User>(this.users);
	}

	public void addUser(User user) {
		this.users.add(user);
	}

	public void updateUser(User user) {
		// unused until JPA
	}

	public void removeUser(User user) {
		if (user == null || !this.users.contains(user)) { return; }

		this.users.remove(user);
	}

	public void createSomeObjects() {
		this.addUser(new User("worker", "password", GroupType.WORKER));
		this.addUser(new User("Gumby", "MyBrainHurts", GroupType.WORKER));
		this.addUser(new User("manager", "password", GroupType.MANAGER));
		ProductType pt = new ProductType("Skumbananer"), pt2 = new ProductType(
				"P-Taerter");
		this.addProductType(pt);
		Stock semi = new Stock("Semi products - main room", StockType.SEMI,
				100, 16, 10), cores = new Stock("Cores machine",
				StockType.MACHINE, 9, 16, 3), coat = new Stock(
				"Coating machine", StockType.MACHINE, 16, 16, 4), done = new Stock(
				"Finished", StockType.FINISHED, 64, 16, 8);
		this.addStock(done);
		this.addStock(semi);
		this.addStock(cores);
		this.addStock(coat);
		SubProcess sp = null;
		pt.addSubProcess(sp = new SubProcess(0, "Core Production", 20, 30, 40));
		sp.addStock(cores);
		pt.addSubProcess(sp = new SubProcess(1, "Core Drying", 20, 30, 40));
		sp.addStock(semi);
		pt.addSubProcess(sp = new SubProcess(2, "Coating", 20, 30, 40));
		sp.addStock(coat);
		pt.addSubProcess(sp = new SubProcess(3, "Drying", 20, 30, 40));
		sp.addStock(semi);
		pt.addSubProcess(sp = new SubProcess(4, "Second Coating", 20, 30, 40));
		sp.addStock(coat);
		pt.addSubProcess(sp = new SubProcess(5, "Last Drying", 20, 30, 40));
		sp.addStock(done);

		pt2.addSubProcess(sp = new SubProcess(0, "Core Production", 20, 30, 40));
		sp.addStock(cores);
		pt2.addSubProcess(sp = new SubProcess(1, "Core Drying", 20, 30, 40));
		sp.addStock(semi);
		pt2.addSubProcess(sp = new SubProcess(2, "Coating", 20, 30, 40));
		sp.addStock(coat);
		pt2.addSubProcess(sp = new SubProcess(3, "Drying", 20, 30, 40));
		sp.addStock(semi);
		pt2.addSubProcess(sp = new SubProcess(4, "Second Coating", 20, 30, 40));
		sp.addStock(coat);
		pt2.addSubProcess(sp = new SubProcess(5, "Last Drying", 20, 30, 40));
		sp.addStock(done);

		sp = pt.getSubProcesses().get(0);
		for (int i = 0; i < 50; i++) {
			StorageUnit su = new StorageUnit(semi, i);
			int cap = (int) Math.ceil(Math.max(.75, Math.random())
					* semi.getMaxTraysPerStorageUnit());
			for (int j = 0; j < cap; j++) {
				Tray tray = new Tray(su, pt, 0);
				State s = new State(tray, new Date(System.currentTimeMillis()));
				s.setSubProcess(sp);
				tray.addState(s);
				su.addTray(tray);
			}
			semi.addStorageUnit(su);
		}
		sp = pt2.getSubProcesses().get(0);
		for (int i = 50; i < 100; i++) {
			StorageUnit su = new StorageUnit(semi, i);
			int cap = (int) Math.ceil(Math.max(.75, Math.random())
					* semi.getMaxTraysPerStorageUnit());
			for (int j = 0; j < cap; j++) {
				Tray tray = new Tray(su, pt2, 0);
				State s = new State(tray, new Date(System.currentTimeMillis()));
				s.setSubProcess(sp);
				tray.addState(s);
				su.addTray(tray);
			}
			semi.addStorageUnit(su);
		}

		for (int i = 0; i < done.getCapacity(); i++) {
			done.addStorageUnit(new StorageUnit(done, i));
		}

		for (int i = 0; i < cores.getCapacity(); i++) {
			cores.addStorageUnit(new StorageUnit(cores, i));
		}

		for (int i = 0; i < coat.getCapacity(); i++) {
			coat.addStorageUnit(new StorageUnit(coat, i));
		}
	}
}
