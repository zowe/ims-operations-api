
/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */

package application.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import application.rest.OMServlet;
import application.rest.services.PgmService;
import application.rest.services.RegionService;
import application.rest.services.TranService;


@SpringBootApplication
public class ApiLauncher {

	public static void main(String[] args) {
		SpringApplication.run(ApiLauncher.class, args);
	}
	
	@Bean
	public OMServlet omServlet() {
		
		return new OMServlet();
		
		
		
	}
	
	@Bean
	public PgmService pgmService() {
		
		return new PgmService();
		
		
		
	}
	
	@Bean
	public TranService tranService() {
		
		return new TranService();
		
		
		
	}
	
	@Bean
	public RegionService regionService() {
		
		return new RegionService();
		
		
		
	}

}