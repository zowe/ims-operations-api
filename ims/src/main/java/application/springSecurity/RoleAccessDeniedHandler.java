package application.springSecurity;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RoleAccessDeniedHandler implements AccessDeniedHandler {



	@Override
	public void handle(
			HttpServletRequest request,
			HttpServletResponse response, 
			AccessDeniedException exc) throws IOException, ServletException {

		Authentication auth 
		= SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			PrintWriter writer = response.getWriter();
			writer.println("HTTP Status 403 - " + exc.getMessage());
			Object princ = auth.getPrincipal();
			String username;
			if (princ instanceof UserDetails) {
				username = ((UserDetails) princ).getUsername();
			} else {
				username = princ.toString();
			}
			writer.println("User: '" + username + "' does not have the required priviliges for this request");
		}

	}
}

