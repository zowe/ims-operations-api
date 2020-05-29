package application.springSecurity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class MyUserDetailsService implements UserDetailsService {

	SpringUsers springUsers;

	@Override
	public User loadUserByUsername(String username) {
		try {
			if (springUsers == null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/users.xml")));

				XmlMapper mapper = new XmlMapper();
				String xml;

				xml = br.lines().collect(Collectors.joining());

				springUsers = mapper.readValue(xml, SpringUsers.class);
			}

			for (application.springSecurity.User u : springUsers.getUser()) {
				if (u.getUsername().equals(username)) {
					return new User(username, u.getPassword(), Arrays.asList(new SimpleGrantedAuthority("ROLE_" + u.getRole())));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new UsernameNotFoundException(username);


	}




}
