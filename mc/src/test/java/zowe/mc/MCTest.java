package zowe.mc;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icon.helpers.MCInteraction;
import json.java.JSONArray;
import json.java.JSONObject;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import om.connection.IconOmConnection;
import om.connection.IconOmConnectionFactory;
import om.exception.OmConnectionException;
import zowe.mc.servlet.OMServlet;

/**
 * Unit test for simple App.
 */
public class MCTest 
extends TestCase
{

	private static final Logger logger = LoggerFactory.getLogger(MCTest.class);
	private MCInteraction mcSpec = new MCInteraction();
	private OMServlet omServlet = new OMServlet();
	
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public MCTest(String testName)
	{
		super( testName );
		
	}
	
	@BeforeClass
	public void setUp() {
		mcSpec.setHostname("EC03173.VMEC.SVL.IBM.COM");
		mcSpec.setPort(7777);
		mcSpec.setImsPlexName("PLEX1");
		mcSpec.getDatastores().add("IMS1");
		mcSpec.getDatastores().add("IMS2");
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( MCTest.class );
	}

	public void testConnection() throws Exception
	{
		logger.info("TESTING MC Connection");
		try {
		
			if (logger.isDebugEnabled()) logger.debug("Creating connection to " + mcSpec.getHostname() + ":" + mcSpec.getPort());

			IconOmConnectionFactory IconCF = new IconOmConnectionFactory();
			IconOmConnection omConnection = IconCF.createIconOmConnectionFromData(mcSpec);
			assertFalse(omConnection.isErrorInConnection());
			
			if (logger.isDebugEnabled()) logger.debug("Connection Successful!"); 
		} catch (OmConnectionException e) {
			logger.error("OmConnectionException", e);
			fail("Connection unsuccessful");
		}

	}

	public void testGetPGM() throws Exception
	{
		logger.info("TESTING MC get pgm");
		String userCommand = "QUERY PGM NAME(*) SHOW(ALL)";
		JSONObject result = omServlet.executeUserImsCommand(userCommand, mcSpec);
		JSONObject message = (JSONObject) result.get("message");
		JSONObject omMessageContext = (JSONObject) message.get("omMessageContext");
		JSONArray executeUserCommand = (JSONArray) omMessageContext.get("executeUserImsCommand");
		JSONObject messageTitle = (JSONObject) executeUserCommand.get(0);
		String text = (String) messageTitle.get("messageTitle");
		assertTrue(text.equals("Operations Manager successfully executed the command."));
		
	}
	
	public void testGetRegions() throws Exception {
		logger.info("TESTING MC get regions");
		String userCommand = "(DISPLAY ACT REGION) OPTION=AOPOUTPUT";
		JSONObject result = omServlet.executeUserImsCommand(userCommand, mcSpec);
		JSONObject message = (JSONObject) result.get("message");
		JSONObject omMessageContext = (JSONObject) message.get("omMessageContext");
		JSONArray executeUserCommand = (JSONArray) omMessageContext.get("executeUserImsCommand");
		JSONObject messageTitle = (JSONObject) executeUserCommand.get(0);
		String text = (String) messageTitle.get("messageTitle");
		assertTrue(text.equals("Operations Manager successfully executed the command."));
		
	}
}
