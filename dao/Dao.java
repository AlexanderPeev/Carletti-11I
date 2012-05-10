package dao;

import java.util.HashSet;
import java.util.Set;

import model.GroupType;
import model.User;

public class Dao {
	private static Dao instance;

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
}
