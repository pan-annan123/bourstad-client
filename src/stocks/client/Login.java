package stocks.client;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import pan.cache.loader.Cache;

public class Login extends LoginDialog {
	
	private static final long serialVersionUID = 1L;
	
	private boolean accepted = false;
	private HashMap<String, String> users;
	private String username;
	private String accesskey;
	
	public Login(Frame parent) {
		super(parent, "Login / Register");
	}

	@Override
	protected void initialize() {
		Cache<HashMap<String, String>> userCache = new Cache<>(Cache.getFolderInHome("Stock Simulation Data"), "users");
		if (userCache.exists()) {
			users = userCache.loadCache();
		} else {
			users = new HashMap<>();
		}
	}

	@Override
	protected void login() {
		if (textField.getText().equals("null") || keyField.getText().equals("null")) return;
		String user = textField.getText();
		String key;
		if (users.containsKey(user)) { // LOGIN USING LOCAL DATA
			System.out.println("local");
			String v = users.get(user);
			accepted = new String(passwordField.getPassword()).equals(v.substring(v.indexOf("=")+1));
			if (accepted) {
				username = user;
				accesskey = v.substring(0, v.indexOf("="));
			}
		}
		else if (!(key = keyField.getText()).isEmpty()) { // LOGIN USING KEY AUTH
			System.out.println("key auth");
			Map<String, String> request = new HashMap<>();
			request.put("action", "keyAuth");
			request.put("key", key);
			Map<String, String> answer = Util.sendRequest(request);
			accepted = Boolean.parseBoolean(answer.get("status"));
			if (accepted) {
				username = answer.get("username");
				accesskey = key;
			}
		}
		else if (user.trim().length() < 5 || user.trim().length() > 20 || !user.replaceAll("[^a-zA-Z0-9\\- ]", "").trim().equals(user)) {
			JOptionPane.showMessageDialog(this, 
					"Your username must have the following properties: \n"
					+ " - have a minimum of 5 and a maximum of 20 characters\n"
					+ " - contain only numbers, letters, - or spaces\n"
					+ " - not start or end with spaces", "Invalid Username", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else {// NEW REGISTRATION
			System.out.println("register");
			Map<String, String> request = new HashMap<>();
			request.put("action", "auth");
			request.put("username", user);
			Map<String, String> answer = Util.sendRequest(request);
			System.out.println(answer);
			accepted = Boolean.parseBoolean(answer.get("status"));
			if (accepted) {
				username = user;
				accesskey = answer.get("key");
				
				//Create entry
				users.put(user, new String(accesskey + "=" + new String(passwordField.getPassword())));
				//Save to local storage
				Cache<HashMap<String, String>> userCache = new Cache<>(Cache.getFolderInHome("Stock Simulation Data"), "users");
				userCache.saveCache(users);
			} else {
				JOptionPane.showMessageDialog(this, "Username is already used.", "Duplicate User", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (accepted) {
			Util.username = username;
			Util.accesskey = accesskey;
			((Origin)getParent()).get().setUser(username);
			((Origin)getParent()).setTitle(Origin.title+" | Username: "+username+" | Key: "+accesskey);
		}
	}
	
	@Override
	protected boolean isLoggedIn() {
		return accepted;
	}
}
