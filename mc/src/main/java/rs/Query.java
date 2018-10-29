package rs;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icon.helpers.MCInteraction;
import json.java.JSONObject;
import om.exception.OmCommandGenerationException;
import utils.Type2CommandSerializable;
import commands.query.pgm.QueryPgm;
import commands.type2.Type2Command;
import zowe.mc.servlet.OMServlet;

@Stateless
@Path("/Query")
public class Query {
	
	@EJB
	OMServlet omServlet;
	
	private static final Logger logger = LoggerFactory.getLogger(Query.class);
	
	@Path("/PGM")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response execute(@QueryParam("name") List<String> name,
			@Context HttpHeaders httpheaders, @QueryParam("show") String show
			) {
		
		MCInteraction mcSpec = new MCInteraction();
		String command = "";
		
		MultivaluedMap<String, String> headers = httpheaders.getRequestHeaders();
		mcSpec.setHostname(headers.get("hostname").get(0));
		mcSpec.setPort(Integer.parseInt(headers.get("port").get(0)));
		mcSpec.setImsPlexName(headers.get("plex").get(0));
		
		QueryPgm pgm = new QueryPgm();
		if (name == null) {
			pgm.getNAME().add("*");
		} else {
			pgm.getNAME().addAll(name);
		}
		
		pgm.getSHOW().add(QueryPgm.ShowOptions.ALL);
		
		Type2Command type2Command = new Type2Command();
		type2Command.setQueryPgm(pgm);
		
		Type2CommandSerializable type2CommandSerializable = new Type2CommandSerializable();
		try {
			String cmd = type2CommandSerializable.fromType2CommandObject(type2Command);
		} catch (OmCommandGenerationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JSONObject result = new JSONObject();
		try {
			result = omServlet.executeUserImsCommand(cmd, mcSpec);
		} catch (Exception e) {
			logger.error("Exception", e);
			Response.serverError();
			e.printStackTrace();
			
		}
		
		return Response.ok(result).build();
		
	}

}
