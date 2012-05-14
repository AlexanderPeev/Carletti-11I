/**
 * @author Ricardas Risys
 */

package model;

import java.util.HashMap;
import java.util.Map;

// blue - too early, green - between min-opt, yellow - opt-max, red - waste
public class User {
	private String username;
	private String password;
	private GroupType group;
	private Map<String, Boolean> permissions = new HashMap<String, Boolean>();

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
			permissions.put("Products", true);
			permissions.put("Logout", true);
			permissions.put("Create Stock", false);
		}
	}
}
