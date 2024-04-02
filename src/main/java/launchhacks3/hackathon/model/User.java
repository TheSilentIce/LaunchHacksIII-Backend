package launchhacks3.hackathon.model;

public class User {
	private String name;
	private String password;

	public User (String name, String password) {
		this.name = name;
		this.password = password; // TODO add salt
	}
}
