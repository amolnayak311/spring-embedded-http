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

import java.util.List;
import static org.springframework.http.embedded.EmbeddedServerType.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * The Factory bean implementation that will be used to generate an instance of
 * {@link EmbeddedServer} based on the type requested
 * @author Amol Nayak
 *
 */
public class EmbeddedServerFactory implements FactoryBean<EmbeddedServer>,InitializingBean {
	
	private static final Log logger = LogFactory.getLog(EmbeddedServerFactory.class);
	
	private ServerConfig config;
	private List<WebApplication> webApplications;
	private EmbeddedServerType type;
	private EmbeddedServer server;
	
	//TODO: Stop the server on context destruction

	@Override
	public void afterPropertiesSet() throws Exception {
		if(server == null) {
			if(JETTY.equals(type)) {
				server = new EmbeddedJettyServer();				
			}	//else.. Other types like TOMCAT goes here
			
			if(server != null) {
				server.initialize(config);
				for(WebApplication webApp:webApplications) {
					server.deployApplication(webApp);
				}
				server.start();
			} else {
				logger.warn("No implementation found for server of type \"" + type + "\"");
			}
		}		
	}

	@Override
	public EmbeddedServer getObject() throws Exception {
		return server;
	}

	@Override
	public Class<?> getObjectType() {		
		return EmbeddedServer.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setConfig(ServerConfig config) {
		this.config = config;
	}

	public void setWebApplications(List<WebApplication> webApplications) {
		this.webApplications = webApplications;
	}
	
	public void setType(EmbeddedServerType type) {
		this.type = type;
	}
}
