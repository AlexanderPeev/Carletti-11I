package model;

public class User {
	private String username;
	private String password;
	private GroupType group;

	public User(String username, String password, GroupType group) {
		setUsername(username);
		setPassword(password);
		setGroup(group);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public GroupType getGroup() {
		return group;
	}

	public void setGroup(GroupType group) {
		this.group = group;
	}

}
