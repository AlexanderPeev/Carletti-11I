package model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @author Ricardas Risys
 */
@Entity(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int id;
	@Column(name = "user_username")
	private String username;
	@Column(name = "user_password")
	private String password;
	@Enumerated(EnumType.STRING)
	@Column(name = "user_group")
	private GroupType group;
	@Transient
	private Map<String, Boolean> permissions = new HashMap<String, Boolean>();

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User() {
	}

	/**
	 * Constructor
	 * 
	 * @param username
	 * @param password
	 * @param group
	 */
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

	/**
	 * Limits user permissions
	 * 
	 * @param key
	 * @return boolean
	 */
	public boolean canAccess(String key) {
		if (permissions.containsKey(key) && permissions.get(key).equals(true)) { return true; }
		return false;
	}

	/**
	 * Apply permissions after user logs in for specific group type
	 */
	public void applyPermissions() {
		if (this.group.equals(GroupType.MANAGER)) {
			permissions.put("Dashboard", true);
			permissions.put("Products", true);
			permissions.put("Statistics", true);
			permissions.put("Create Trays", true);
			permissions.put("Logout", true);
			permissions.put("Create Stock", true);
			permissions.put("Update Stock", true);
			permissions.put("Delete Stock", true);
		}
		else if (this.group.equals(GroupType.WORKER)) {
			permissions.put("Dashboard", true);
			permissions.put("Create Trays", true);
			permissions.put("Logout", true);
			permissions.put("Create Stock", false);
		}
	}
}
