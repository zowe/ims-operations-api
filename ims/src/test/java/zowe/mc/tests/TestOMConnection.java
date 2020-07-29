
/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */

package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.pgm.query.QueryProgramOutput;
import icon.helpers.MCInteraction;
import om.connection.IconOmConnection;
import om.connection.IconOmConnectionFactory;
import om.exception.OmConnectionException;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for IMS connections
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestOMConnection {

	private static MCInteraction mcSpec = new MCInteraction();
	private static final Logger logger = LoggerFactory.getLogger(TestOMConnection.class);
	private static Client client;
	private final String DEFAULT_USER = "admin";
	private final String DEFAULT_PASSWORD = "password";

	@BeforeAll
	public static void setUp() {

		mcSpec.setHostname(TestProperties.hostname);
		mcSpec.setPort(TestProperties.port);
		mcSpec.setImsPlexName(TestProperties.plex);
		setClient(ClientBuilder.newClient());
	}

	/**
	 * Test connection to IMS
	 */
	@Test
	public void testOmConnection()
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


	/**
	 * Negative test for bad PLEX
	 */
	@Test
	public void testBadPlex() {

		logger.info("TESTING BAD PLEX NAME");		

		//SHOW=TIMESTAMP
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(),  TestProperties.contextPath + "FOO" + "/program", DEFAULT_USER, DEFAULT_PASSWORD);

		QueryProgramOutput queryProgramResponses= response.readEntity(QueryProgramOutput.class);
		
		//logger.info(queryProgramResponses.toString());
		assertNotEquals(null, queryProgramResponses);
		assertEquals(400, response.getStatus());	

		for (String key : queryProgramResponses.getMessages().keySet()) {
			assertEquals("4", queryProgramResponses.getMessages().get(key).getRc());
		}
	}

	public static Client getClient() {
		return client;
	}

	public static void setClient(Client client) {
		TestOMConnection.client = client;
	}

}
