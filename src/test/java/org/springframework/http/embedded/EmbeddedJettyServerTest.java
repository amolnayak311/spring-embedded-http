/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.http.embedded;

import java.io.File;
import java.util.Collections;

import org.junit.Test;

/**
 * The test class for the embedded jetty server
 * @author Amol Nayak
 *
 */
public class EmbeddedJettyServerTest {

	//@Test
	public void deploySingleServlet() throws Exception {
		EmbeddedServer server = new EmbeddedJettyServer();
		ServerConfig config = new ServerConfig();	//Default one
		
		//initialize
		server.initialize(config);
		//deploy an app with context test
		deployUsingServlet(server);		
		//start
		server.start();
		//Quickly open the browser within 10 seconds and type in http://localhost:8080/test/hello
		Thread.sleep(5000);
		//stop
		server.stop();		
	}

	private void deployUsingServlet(EmbeddedServer server) throws Exception {
		WebApplication webApp = new WebApplication();
		webApp.setServletMappings(Collections.singletonList(
						new ServletMapping(new TestHttpServlet(), "/hello")));
		webApp.setContextPath("/test");
		webApp.afterPropertiesSet();
		server.deployApplication(webApp);
	}
	
	//@Test
	public void deployWar() throws Exception {
		EmbeddedServer server = new EmbeddedJettyServer();
		ServerConfig config = new ServerConfig();	//Default one
		
		//initialize
		server.initialize(config);
		//deploy an app with context test
		deployUsingWar(server);		
		//start
		server.start();
		//Quickly open the browser within 10 seconds and type in http://localhost:8080/testwebapp/hello
		Thread.sleep(5000);
		//stop
		server.stop();
	}

	private void deployUsingWar(EmbeddedServer server) throws Exception {
		WebApplication webApp = new WebApplication();
		webApp.setWarPath("classpath:testapp.war");
		webApp.setWarPath("/testwebapp");
		webApp.afterPropertiesSet();
		server.deployApplication(webApp);
	}
	
	@Test
	public void deployFromRoot() throws Exception {
		File file = new File(".");
		System.out.println(file.getAbsolutePath());
		EmbeddedServer server = new EmbeddedJettyServer();
		ServerConfig config = new ServerConfig();	//Default one
		
		//initialize
		server.initialize(config);
		//deploy an app with context test
		deployUsingWebappRoot(server);		
		//start
		server.start();
		//Quickly open the browser within 10 seconds and type in http://localhost:8080/testwebapproot/hello
		Thread.sleep(5000);
		//stop
		server.stop();
	}

	private void deployUsingWebappRoot(EmbeddedServer server) throws Exception {
		WebApplication webApp = new WebApplication();
		webApp.setWebAppRoot("src/test/resources/testwebapproot");
		webApp.setContextPath("/testwebapproot");
		webApp.afterPropertiesSet();
		server.deployApplication(webApp);
	}
}
