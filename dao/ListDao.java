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

public class ListDao extends Dao {
	private Set<Stock> stocks = new HashSet<Stock>();
	private Set<ProductType> productTypes = new HashSet<ProductType>();
	private Set<User> users = new HashSet<User>();

	ListDao() {
		createSomeObjects();
	}

	@Override
	public Set<Stock> getStocks() {
		return new HashSet<Stock>(this.stocks);
	}

	@Override
	public Iterator<Stock> getStocksIterator() {
		return this.stocks.iterator();
	}

	@Override
	public int getStocksTotal() {
		return this.stocks.size();
	}

	@Override
	public void addStock(Stock stock) {
		this.stocks.add(stock);
	}

	@Override
	public void removeStock(Stock stock) {
		this.stocks.remove(stock);
	}

	@Override
	public void updateStock(Stock stock) {
		// unused until JPA

	}

	@Override
	public void addProductType(ProductType productType) {
		productTypes.add(productType);
	}

	@Override
	public void updateProductType(ProductType productType) {

		// unused until JPA
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dao.Dao#removeProductType(model.ProductType)
	 */
	@Override
	public void removeProductType(ProductType productType) {
		if (productType != null && productTypes.contains(productType)) productTypes
				.remove(productType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dao.Dao#getAllProductTypes()
	 */
	@Override
	public Set<ProductType> getAllProductTypes() {
		return new HashSet<ProductType>(productTypes);// return a copy and not
														// the original
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dao.Dao#addState(model.SubProcess, model.State)
	 */
	@Override
	public void addState(SubProcess subProcess, State state) {
		state.setSubProcess(subProcess);
	}

	@Override
	public void updateState(State state) {
		// unused until JPA

	}

	@Override
	public void addSubProcess(ProductType productType, SubProcess subProcess) {
		productType.addSubProcess(subProcess);
	}

	@Override
	public void updateSubProcess(SubProcess subProcess) {
		// unused until JPA
	}

	@Override
	public void removeSubProcess(ProductType productType, SubProcess subProcess) {
		productType.removeSubProcess(subProcess);
	}

	@Override
	public Set<User> getUsers() {
		return new HashSet<User>(this.users);
	}

	@Override
	public void addUser(User user) {
		this.users.add(user);
	}

	@Override
	public void updateUser(User user) {
		// unused until JPA
	}

	@Override
	public void removeUser(User user) {
		if (user == null || !this.users.contains(user)) { return; }

		this.users.remove(user);
	}

	@Override
	public void addTray(Tray tray) {
		// unused until JPA
	}

	@Override
	public void updateTray(Tray tray) {
		// unused until JPA
	}

	@Override
	public void removeTray(Tray tray) {
		// unused until JPA
	}

	@Override
	public void createSomeObjects() {
		this.addUser(new User("worker", "password", GroupType.WORKER));
		this.addUser(new User("Gumby", "MyBrainHurts", GroupType.WORKER));
		this.addUser(new User("manager", "password", GroupType.MANAGER));
		ProductType pt = new ProductType("Skumbananer"), pt2 = new ProductType(
				"P-Taerter");
		Stock semi = new Stock("Semi products - main room", StockType.SEMI,
				100, 16, 10), cores = new Stock("Cores machine",
				StockType.MACHINE, 9, 16, 3), coat = new Stock(
				"Coating machine", StockType.MACHINE, 16, 16, 4), done = new Stock(
				"Finished", StockType.FINISHED, 64, 16, 8);
		SubProcess sp = null;
		pt.addSubProcess(sp = new SubProcess(0, "Core Production", 2, 4, 6));
		sp.addStock(cores);
		pt.addSubProcess(sp = new SubProcess(1, "Core Drying", 2, 4, 6));
		sp.addStock(semi);
		pt.addSubProcess(sp = new SubProcess(2, "Coating", 2, 4, 6));
		sp.addStock(coat);
		pt.addSubProcess(sp = new SubProcess(3, "Drying", 2, 4, 6));
		sp.addStock(semi);
		pt.addSubProcess(sp = new SubProcess(4, "Second Coating", 2, 4, 6));
		sp.addStock(coat);
		pt.addSubProcess(sp = new SubProcess(5, "Last Drying", 2, 4, 6));
		sp.addStock(done);

		pt2.addSubProcess(sp = new SubProcess(0, "Core Production", 2, 4, 6));
		sp.addStock(cores);
		pt2.addSubProcess(sp = new SubProcess(1, "Core Drying", 2, 4, 6));
		sp.addStock(semi);
		pt2.addSubProcess(sp = new SubProcess(2, "Coating", 2, 4, 6));
		sp.addStock(coat);
		pt2.addSubProcess(sp = new SubProcess(3, "Drying", 2, 4, 6));
		sp.addStock(semi);
		pt2.addSubProcess(sp = new SubProcess(4, "Second Coating", 2, 4, 6));
		sp.addStock(coat);
		pt2.addSubProcess(sp = new SubProcess(5, "Last Drying", 2, 4, 6));
		sp.addStock(done);

		sp = pt.getSubProcesses().get(0);
		for (int i = 0; i < 5; i++) {
			StorageUnit su = new StorageUnit(cores, i);
			int cap = (int) Math.ceil(Math.max(.75, Math.random())
					* cores.getMaxTraysPerStorageUnit());
			for (int j = 0; j < cap; j++) {
				Tray tray = new Tray(su, pt, 0);
				State s = new State(tray, new Date(System.currentTimeMillis()));
				s.setSubProcess(sp);
				tray.addState(s);
				su.addTray(tray);
				this.addTray(tray);
			}
			cores.addStorageUnit(su);
		}
		sp = pt2.getSubProcesses().get(0);
		for (int i = 5; i < 9; i++) {
			StorageUnit su = new StorageUnit(cores, i);
			int cap = (int) Math.ceil(Math.max(.75, Math.random())
					* cores.getMaxTraysPerStorageUnit());
			for (int j = 0; j < cap; j++) {
				Tray tray = new Tray(su, pt2, 0);
				State s = new State(tray, new Date(System.currentTimeMillis()));
				s.setSubProcess(sp);
				tray.addState(s);
				su.addTray(tray);
				this.addTray(tray);
			}
			cores.addStorageUnit(su);
		}

		for (int i = 0; i < done.getCapacity(); i++) {
			done.addStorageUnit(new StorageUnit(done, i));
		}

		for (int i = 0; i < semi.getCapacity(); i++) {
			semi.addStorageUnit(new StorageUnit(semi, i));
		}

		for (int i = 0; i < coat.getCapacity(); i++) {
			coat.addStorageUnit(new StorageUnit(coat, i));
		}
		this.addProductType(pt);
		this.addProductType(pt2);
		this.addStock(done);
		this.addStock(semi);
		this.addStock(cores);
		this.addStock(coat);
	}

	@Override
	public void assignSubProcessToStock(Stock stock, SubProcess subProcess) {
		stock.addSubProcess(subProcess);
		subProcess.addStock(stock);
	}

	@Override
	public void unsignSubProcessFromStock(Stock stock, SubProcess subProcess) {
		stock.removeSubProcess(subProcess);
		subProcess.removeStock(stock);

	}

	@Override
	public void addStorageUnit(StorageUnit unit) {
		// unused until JPA
	}

	@Override
	public void removeStorageUnit(StorageUnit unit) {
		// unused until JPA
	}
}
