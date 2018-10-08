package execute;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icon.helpers.MCInteraction;
import zowe.mc.servlet.OMServlet;

@Stateless
@Path("/executeCommand")
public class ExecuteCommand {
	
	@EJB
	OMServlet omServlet;
	
	private static final Logger logger = LoggerFactory.getLogger(ExecuteCommand.class);
	
	@Path("/submit")
	@POST
	@Consumes("application/json")
	public Response execute(@QueryParam("command") String command,
			@QueryParam("plex") String plex,
			@QueryParam("datastores") List<String> datastores) {
		
		
		MCInteraction mcSpec = new MCInteraction();
		mcSpec.setImsPlexName("PLEX1");
		for (String s : datastores) {
			mcSpec.getDatastores().add(s);
		}
		
		try {
			omServlet.executeUserImsCommand(command, mcSpec);
		} catch (Exception e) {
			logger.error("Exception", e);
			e.printStackTrace();
		}
		
		
		return Response.status(200).build();
		
	}

}
