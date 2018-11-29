package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icon.helpers.MCInteraction;
import om.connection.IconOmConnection;
import om.connection.IconOmConnectionFactory;
import om.exception.OmConnectionException;
import zowe.mc.TestProperties;

/**
 * Tests for IMS connections
 * @author jerryli
 *
 */
public class OMConnectionTest {

	private static MCInteraction mcSpec = new MCInteraction();
	private static final Logger logger = LoggerFactory.getLogger(OMConnectionTest.class);

	@BeforeAll
	public static void setUp() {
		
		mcSpec.setHostname(TestProperties.hostname);
		mcSpec.setPort(TestProperties.port);
		mcSpec.setImsPlexName(TestProperties.plex);
	}

	/**
	 * Test connection to IMS
	 * @throws Exception
	 */
	@Test
	public void testImsConnection() throws Exception
	{
		logger.info("TESTING MC Connection");
		try {

			if (logger.isDebugEnabled()) logger.debug("Creating connection to " + mcSpec.getHostname() + ":" + mcSpec.getPort() + " - " + TestProperties.plex);

			IconOmConnectionFactory IconCF = new IconOmConnectionFactory();
			IconOmConnection omConnection = IconCF.createIconOmConnectionFromData(mcSpec);
			assertFalse(omConnection.isErrorInConnection());

			if (logger.isDebugEnabled()) logger.debug("Connection Successful!"); 
		} catch (OmConnectionException e) {
			logger.error("OmConnectionException", e);
			fail("Connection unsuccessful");
		}

	}
}
