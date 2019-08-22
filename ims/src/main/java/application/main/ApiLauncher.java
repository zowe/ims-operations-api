
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
import org.springframework.context.annotation.ComponentScan;

import com.ca.mfaas.enable.EnableApiDiscovery;

@SpringBootApplication
@ComponentScan({"com.ca.mfaas.enable", "com.ca.mfaas.product", "application.main", "application.services", "application.models", "application.resources", "application.rest", "springSecurity"})
@EnableApiDiscovery
public class ApiLauncher {
	
	public static void main(String[] args) {
		SpringApplication.run(ApiLauncher.class, args);
	}
	
}