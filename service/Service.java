package service;

import java.util.Set;

import javax.security.auth.login.LoginException;

import model.User;
import dao.Dao;

public class Service {
	private static User currentUser = null;
	private static Dao dao = Dao.getInstance();

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

	public static User getCurrentUser() {
		return Service.currentUser;
	}

	public static void setCurrentUser(User currentUser) {
		Service.currentUser = currentUser;
	}
}
