package springSecurity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;


public class SpringUsers {
	
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<User> user = new ArrayList<User>();

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}
	
	
	
	
	

}
