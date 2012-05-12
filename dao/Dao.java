package dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import model.GroupType;
import model.Stock;
import model.User;

public class Dao {
	private static Dao instance;
	private Set<Stock> stocks;

	public static Dao getInstance() {
		if (Dao.instance == null) Dao.instance = new Dao();
		return Dao.instance;
	}

	private Dao() {

	}

	public Set<User> getUsers() {
		Set<User> users = new HashSet<User>();
		users.add(new User("worker", "password", GroupType.WORKER));
		users.add(new User("Gumby", "MyBrainHurts", GroupType.WORKER));
		users.add(new User("manager", "password", GroupType.MANAGER));
		return users;
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

}
