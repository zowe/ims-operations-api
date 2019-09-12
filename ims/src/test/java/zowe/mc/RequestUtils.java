
/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */

package zowe.mc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import application.rest.responses.pgm.create.CreateProgramOutput;
import application.rest.responses.pgm.delete.DeleteProgramOutput;
import application.rest.responses.pgm.query.QueryProgramOutput;
import application.rest.responses.pgm.start.StartProgramOutput;
import application.rest.responses.pgm.update.UpdateProgamOutput;
import application.rest.responses.tran.create.CreateTransactionOutput;
import application.rest.responses.tran.delete.DeleteTransactionOutput;
import application.rest.responses.tran.query.QueryTransactionOutput;
import application.rest.responses.tran.start.StartTransactionOutput;
import application.rest.responses.tran.update.UpdateTransactionOutput;


public class RequestUtils {


	private static String port;
	private static Client client;
	private static String urlPrefix;
	private static String host;



	static {
		ResourceBundle appProperties = ResourceBundle.getBundle("testConfiguration");
		port = appProperties.getString("server.port");
		host = appProperties.getString("server.host");
		ClientBuilder clientBuilder = ClientBuilder.newBuilder();

		if (appProperties.getString("server.ssl.enabled").equalsIgnoreCase("true")) {
			urlPrefix = "https://";

			/*
			 * For our junit test clients we don't care about if the certificate is valid or not.
			 * We just want to test functionality of the REST APIs. To be clear, the below method of
			 * ignoring ssl certificates is strongly not recommended to use in production client code,
			 * however because these are just junit tests it's fine. 
			 */

			try {
				TrustManager[] trustManager = new X509TrustManager[] { new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					@Override
					public void checkClientTrusted(X509Certificate[] certs, String authType) {

					}
					@Override
					public void checkServerTrusted(X509Certificate[] certs, String authType) {
					}
				}};

				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustManager, null);
				client = ClientBuilder.newBuilder().sslContext(sslContext).build();

			}
			catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			urlPrefix = "http://";
			client = clientBuilder.build();
		}


	}

	public static Response postRequest(List<String[]> queryParams, String path, String username, String password) {

		WebTarget webTarget = client.target(urlPrefix + host + ":" + port);

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		webTarget.register(new JacksonJsonProvider());
		webTarget.register(new Authenticator(username, password));
		Invocation.Builder builder =  webTarget.path(path).request(MediaType.APPLICATION_JSON)
				.header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.accept(MediaType.APPLICATION_JSON);

		//Apache CXF does not like passing Entity.json(null)
		Response responses = builder.post(Entity.text(""));
		return responses;

	}

	public static Response deleteRequest(List<String[]> queryParams, String path, String username, String password) {

		WebTarget webTarget = client.target(urlPrefix + host + ":" + port);

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		webTarget.register(new JacksonJsonProvider());
		webTarget.register(new Authenticator(username, password));
		Invocation.Builder builder =  webTarget.path(path).request(MediaType.APPLICATION_JSON)
				.header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.accept(MediaType.APPLICATION_JSON);

		Response responses = builder.delete();
		return responses;

	}

	public static Response putRequest(List<String[]> queryParams, String path, String username, String password) {

		WebTarget webTarget = client.target(urlPrefix + host + ":" + port);

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}
		webTarget.register(new JacksonJsonProvider());
		webTarget.register(new Authenticator(username, password));
		Invocation.Builder builder =  webTarget.path(path).request(MediaType.APPLICATION_JSON)
				.header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.accept(MediaType.APPLICATION_JSON);

		//Apache CXF does not like passing Entity.json(null)
		Response responses = builder.put(Entity.text(""));
		return responses;
	}

	/**
	 * Helper method for testing successful 200 rest requests. Specific to this class
	 * @param queryParams
	 * @return
	 */
	public static Response getRequest(List<String[]> queryParams, String path, String username, String password) {
		WebTarget webTarget = client.target(urlPrefix + host + ":" + port);

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		webTarget.register(new JacksonJsonProvider());
		webTarget.register(new Authenticator(username, password));
		Invocation.Builder builder =  webTarget.path(path).request(MediaType.APPLICATION_JSON).header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.accept(MediaType.APPLICATION_JSON);

		Response responses = builder.get();
		return responses;
	}
	
	
	/**
	 * Helper method for testing successful 200 rest requests. Specific to this class
	 * @param queryParams
	 * @param path
	 * @param username 
	 * @param password
	 * @return
	 */
	public static Response customGetRequest(List<String[]> queryParams, String path, String username, String password) {
		WebTarget webTarget = client.target(urlPrefix + host + ":" + port);

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		webTarget.register(new JacksonJsonProvider());
		webTarget.register(new Authenticator(username, password));
		Invocation.Builder builder =  webTarget.path(path).request(MediaType.APPLICATION_JSON).header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.accept(MediaType.APPLICATION_JSON);

		Response responses = builder.get();
		return responses;
	}

	public static UpdateTransactionOutput validateUTRSuccess(Response responses) {
		UpdateTransactionOutput updateTranResponses = responses.readEntity(UpdateTransactionOutput.class);
		assertNotEquals(null, updateTranResponses);
		assertNotEquals(0, updateTranResponses.getData().size());
		assertEquals("0", updateTranResponses.getData().get(0).getCc());
		assertEquals(200, responses.getStatus());
		return updateTranResponses;

	}

	public static StartTransactionOutput validateSTRSuccess(Response response) {
		StartTransactionOutput startTransactionResponses = response.readEntity(StartTransactionOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, startTransactionResponses);
		assertNotEquals(0, startTransactionResponses.getData().size());
		assertEquals(false, startTransactionResponses.getData().get(0).get().isEmpty());
		assertEquals(200, response.getStatus());
		return startTransactionResponses;
	}

	public static CreateTransactionOutput validateCTRSuccess(Response response) {
		CreateTransactionOutput createTransactionResponses = response.readEntity(CreateTransactionOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, createTransactionResponses);
		assertNotEquals(0, createTransactionResponses.getData().size());
		assertEquals("0", createTransactionResponses.getData().get(0).getCc());
		assertEquals(200, response.getStatus());
		return createTransactionResponses;
	}

	public static DeleteTransactionOutput validateDTRSuccess(Response response) {
		DeleteTransactionOutput deleteTransactionResponses = response.readEntity(DeleteTransactionOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, deleteTransactionResponses);
		assertNotEquals(0, deleteTransactionResponses.getData().size());
		assertEquals("0", deleteTransactionResponses.getData().get(0).getCc());
		assertEquals(200, response.getStatus());
		return deleteTransactionResponses;
	}

	public static UpdateProgamOutput validateUPRSuccess(Response responses) {
		UpdateProgamOutput updatePgmResponses = responses.readEntity(UpdateProgamOutput.class);
		assertNotEquals(null, updatePgmResponses);
		assertNotEquals(0, updatePgmResponses.getData().size());
		assertEquals("0", updatePgmResponses.getData().get(0).getCc());
		assertEquals(200, responses.getStatus());
		return updatePgmResponses;

	}

	public static QueryProgramOutput validateQPRSuccess(Response responses){
		QueryProgramOutput queryPgmResponses = responses.readEntity(QueryProgramOutput.class);
		assertNotEquals(null, queryPgmResponses);
		assertNotEquals(0, queryPgmResponses.getData().size());
		assertEquals("0", queryPgmResponses.getData().get(0).getCc());
		assertEquals(200, responses.getStatus());
		return queryPgmResponses;
	}

	public static StartProgramOutput validateSPRSuccess(Response response) {
		StartProgramOutput startProgramResponses = response.readEntity(StartProgramOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, startProgramResponses);
		assertNotEquals(0, startProgramResponses.getData().size());
		assertEquals(false, startProgramResponses.getData().get(0).get().isEmpty());
		assertEquals(200, response.getStatus());
		return startProgramResponses;
	}

	public static CreateProgramOutput validateCPRSuccess(Response response) {
		CreateProgramOutput createProgramResponses = response.readEntity(CreateProgramOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, createProgramResponses);
		assertNotEquals(0, createProgramResponses.getData().size());
		assertEquals("0", createProgramResponses.getData().get(0).getCc());
		assertEquals(200, response.getStatus());
		return createProgramResponses;
	}

	public static DeleteProgramOutput validateDPRSuccess(Response response) {
		DeleteProgramOutput deleteProgramResponses = response.readEntity(DeleteProgramOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, deleteProgramResponses);
		assertNotEquals(0, deleteProgramResponses.getData().size());
		assertEquals("0", deleteProgramResponses.getData().get(0).getCc());
		assertEquals(200, response.getStatus());
		return deleteProgramResponses;
	}

	public static QueryTransactionOutput validateQTRSuccess(Response response) {
		QueryTransactionOutput queryTranResponses = response.readEntity(QueryTransactionOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, queryTranResponses);
		assertNotEquals(0, queryTranResponses.getData().size());
		assertEquals("0", queryTranResponses.getData().get(0).getCc());
		assertEquals(200, response.getStatus());
		return queryTranResponses;
	}

	public static HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory()
	{
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
		= new HttpComponentsClientHttpRequestFactory();

		clientHttpRequestFactory.setHttpClient(httpClient());

		return clientHttpRequestFactory;
	}

	public static HttpClient httpClient()
	{
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials("admin", "password"));

		HttpClient client = HttpClientBuilder
				.create()
				.setDefaultCredentialsProvider(credentialsProvider)
				.build();
		return client;
	}
	
	public static HttpHeaders createHeaders(String username, String password){
		   return new HttpHeaders() {/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
		         String auth = username + ":" + password;
		         byte[] encodedAuth = Base64.encodeBase64( 
		            auth.getBytes(Charset.forName("US-ASCII")) );
		         String authHeader = "Basic " + new String( encodedAuth );
		         set( "Authorization", authHeader );
		      }};
		}


}
