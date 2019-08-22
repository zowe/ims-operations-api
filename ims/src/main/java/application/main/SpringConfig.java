
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



import java.util.Arrays;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import application.LibertyApp;
import application.rest.OMServlet;
import application.rest.services.PgmService;
import application.rest.services.RegionService;
import application.rest.services.TranService;
import filters.AuthRequestFilter;



@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "application")
public class SpringConfig {

	@Autowired
    private Bus bus;
	
	
	@Bean
    public Server jaxRsServer() {
        final JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setApplication(new LibertyApp());
        factory.setServiceBeans(Arrays.<Object>asList(new AuthRequestFilter(), new PgmService(), new TranService(), new RegionService(), new OMServlet()));
        factory.setProvider(new JacksonJsonProvider());
        factory.setFeatures(Arrays.asList(new OpenApiFeature()));
        factory.setBus(bus);
        return factory.create();
    }
    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServlet() {
        final ServletRegistrationBean<CXFServlet> servletRegistrationBean = new ServletRegistrationBean<CXFServlet>(new CXFServlet());
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

}
