package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import model.GroupType;
import model.ProductType;
import model.State;
import model.Stock;
import model.StockType;
import model.StorageUnit;
import model.SubProcess;
import model.Tray;
import model.User;

public class JpaDao extends Dao {
	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tx;

	public JpaDao() {
		boolean create = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/", "carletti", "carletti");

			Statement stmt = connection.createStatement();
			try {
				stmt.executeUpdate("USE carletti;");
				ResultSet rs = stmt
						.executeQuery("SELECT * from users LIMIT 1;");
				if (!rs.next()) throw new Exception("RECREATE!!!");
				System.out.println("Database exists!");
			}
			catch (Throwable e) {
				stmt.executeUpdate("DROP DATABASE IF EXISTS carletti;");
				stmt.executeUpdate("CREATE DATABASE carletti;");
				stmt.executeUpdate("USE carletti;");
				System.out.println("Database created!");
				create = true;
			}

		}
		catch (Exception e) {
			System.out.println("Error interacting with database:  "
					+ e.getMessage());
		}
		emf = Persistence.createEntityManagerFactory("Carletti");
		em = emf.createEntityManager();
		tx = em.getTransaction();
		if (create) createSomeObjects();
	}

	@Override
	public Set<Stock> getStocks() {
		return new HashSet<Stock>(em.createQuery("SELECT s FROM stocks s",
				Stock.class).getResultList());
	}

	@Override
	public Iterator<Stock> getStocksIterator() {
		return em.createQuery("SELECT s FROM stocks s", Stock.class)
				.getResultList().iterator();
	}

	@Override
	public int getStocksTotal() {
		return em.createQuery("SELECT count(1) FROM stocks s", Integer.class)
				.getSingleResult();
	}

	@Override
	public void addStock(Stock stock) {
		tx.begin();
		em.persist(stock);
		tx.commit();
	}

	@Override
	public void removeStock(Stock stock) {
		tx.begin();
		for (SubProcess sp : stock.getSubProcesses()) {
			stock.removeSubProcess(sp);
		}
		for (StorageUnit su : stock.getStorageUnits()) {
			stock.removeStorageUnit(su);
		}
		em.merge(stock);
		em.remove(stock);
		tx.commit();
	}

	@Override
	public void updateStock(Stock stock) {
		tx.begin();
		em.merge(stock);
		tx.commit();
	}

	@Override
	public void addProductType(ProductType productType) {
		tx.begin();
		em.persist(productType);
		tx.commit();
	}

	@Override
	public void updateProductType(ProductType productType) {
		tx.begin();
		em.merge(productType);
		tx.commit();
	}

	@Override
	public void removeProductType(ProductType productType) {
		tx.begin();
		for (SubProcess sp : productType.getSubProcesses()) {
			for (Stock s : sp.getStocks()) {
				sp.removeStock(s);
			}
			em.merge(sp);
			em.remove(sp);
		}
		em.remove(productType);
		tx.commit();
	}

	@Override
	public Set<ProductType> getAllProductTypes() {
		return new HashSet<ProductType>(em.createQuery(
				"SELECT pt FROM product_types pt", ProductType.class)
				.getResultList());
	}

	@Override
	public void addState(SubProcess subProcess, State state) {
		state.setSubProcess(subProcess);
		tx.begin();
		if (em.contains(state)) em.merge(state);
		else em.persist(state);
		tx.commit();
	}

	@Override
	public void updateState(State state) {
		tx.begin();
		em.merge(state);
		tx.commit();
	}

	@Override
	public void addSubProcess(ProductType productType, SubProcess subProcess) {
		productType.addSubProcess(subProcess);
		tx.begin();
		if (em.contains(subProcess)) em.merge(subProcess);
		else em.persist(subProcess);
		tx.commit();
	}

	@Override
	public void updateSubProcess(SubProcess subProcess) {
		tx.begin();
		em.merge(subProcess);
		tx.commit();
	}

	@Override
	public void removeSubProcess(ProductType productType, SubProcess subProcess) {
		productType.removeSubProcess(subProcess);
		tx.begin();
		for (Stock s : subProcess.getStocks()) {
			subProcess.removeStock(s);
		}
		TypedQuery<State> q = em.createQuery(
				"SELECT s FROM states s WHERE s.subProcess=:sp", State.class);
		q.setParameter("sp", subProcess);
		List<State> states = q.getResultList();
		for (State state : states) {
			em.remove(state);
		}
		em.merge(subProcess);
		em.remove(subProcess);
		tx.commit();
	}

	@Override
	public Set<User> getUsers() {
		return new HashSet<User>(em.createQuery("SELECT u FROM users u",
				User.class).getResultList());
	}

	@Override
	public void addUser(User user) {
		tx.begin();
		em.persist(user);
		tx.commit();
	}

	@Override
	public void updateUser(User user) {
		tx.begin();
		em.merge(user);
		tx.commit();
	}

	@Override
	public void removeUser(User user) {
		tx.begin();
		em.remove(user);
		tx.commit();
	}

	@Override
	public void addTray(Tray tray) {
		tx.begin();
		em.persist(tray);
		tx.commit();
	}

	@Override
	public void updateTray(Tray tray) {
		tx.begin();
		em.merge(tray);
		tx.commit();
	}

	@Override
	public void removeTray(Tray tray) {
		tx.begin();
		for (State state : tray.getStates()) {
			tray.removeState(state);
			em.remove(state);
		}
		em.merge(tray);
		em.remove(tray);
		tx.commit();
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
		tx.begin();
		em.merge(stock);
		em.merge(subProcess);
		tx.commit();
	}

	@Override
	public void unsignSubProcessFromStock(Stock stock, SubProcess subProcess) {
		stock.removeSubProcess(subProcess);
		subProcess.removeStock(stock);
		tx.begin();
		em.merge(stock);
		em.merge(subProcess);
		tx.commit();
	}

	@Override
	public void addStorageUnit(StorageUnit unit) {
		tx.begin();
		em.persist(unit);
		tx.commit();
	}

	@Override
	public void removeStorageUnit(StorageUnit unit) {
		tx.begin();
		em.remove(unit);
		tx.commit();
	}

}
