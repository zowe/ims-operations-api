package springSecurity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class User {

	private String username;
	private String password;
	private String role;

	@JacksonXmlProperty(isAttribute=true)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@JacksonXmlProperty(isAttribute=true)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@JacksonXmlProperty(isAttribute=true)
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
}
