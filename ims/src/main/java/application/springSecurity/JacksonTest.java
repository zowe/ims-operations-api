package application.springSecurity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class JacksonTest {
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		SpringUsers springUsers = new SpringUsers();
		User user = new User();
		user.setUsername("admin");
		user.setPassword("password");
		user.setRole("test");
		
		User user1 = new User();
		user1.setUsername("jerry");
		user1.setPassword("password");
		user1.setRole("admin");
		
		springUsers.getUser().add(user);
		springUsers.getUser().add(user1);
		
		
		try {
			XmlMapper mapper = new XmlMapper();
			String xml = mapper.writeValueAsString(springUsers);
			System.out.println(xml);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(JacksonTest.class.getResourceAsStream("/users.xml")));

		XmlMapper mapper = new XmlMapper();
		String xml;

		xml = br.lines().collect(Collectors.joining());

		try {
			springUsers = mapper.readValue(xml, SpringUsers.class);
			
			System.out.println(springUsers);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	

}
