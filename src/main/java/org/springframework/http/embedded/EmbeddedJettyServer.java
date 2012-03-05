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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * The concrete implementation that will start the embedded jetty server
 * @author Amol Nayak
 *
 */
public class EmbeddedJettyServer extends AbstractEmbeddedServer {

	private Server server;
	private List<Handler> handlers = new ArrayList<Handler>();
	
	/* (non-Javadoc)
	 * @see org.springframework.http.embedded.AbstractEmbeddedServer#doStart()
	 */
	@Override
	protected void doStart() throws Exception {
		if(server == null)	//Will be null only if no app is deployed on it
			server = new Server(config.getStartupPort());
		if(!handlers.isEmpty()) {
			//Add these to the server
			ContextHandlerCollection handlerCollection = new ContextHandlerCollection(); 
			handlerCollection.setHandlers(handlers.toArray(new Handler[]{}));
			server.setHandler(handlerCollection);
		}
		server.start();
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.http.embedded.AbstractEmbeddedServer#doStop()
	 */
	@Override
	protected void doStop() throws Exception {		
		server.stop();
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.embedded.AbstractEmbeddedServer#doDeployApplication(org.springframework.http.embedded.WebApplication)
	 */
	@Override
	protected boolean doDeployApplication(WebApplication webapp) {
		
		String contextPath = webapp.getContextPath();
		//Server can contain multiple web apps some with servlets only and some with war
		List<ServletMapping> servletMappings = webapp.getServletMappings();
		if(webapp.isServletMappingGiven()) {
			//add an application with this servlet to the server
			//TODO: See how more options can be obtained 
			ServletContextHandler sHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
			sHandler.setContextPath(contextPath);
			//Iterate
			for(ServletMapping mapping:servletMappings) {
				sHandler.addServlet(new ServletHolder(mapping.getServlet()), mapping.getUrlPattern());
			}
			handlers.add(sHandler);
		} else if(webapp.isWarPathGiven()){
			//The path can be a .war file or 
			WebAppContext wContext = new WebAppContext();
			wContext.setContextPath(contextPath);
			String fileWar;
			try {
				fileWar = webapp.getResource().getFile().getAbsolutePath();
			} catch (IOException e) {
				throw new DeploymentException("Unable to get the URL to the resource, \"" + webapp.getResource().getFilename() + "\" ", e);
			}
			
			if(logger.isInfoEnabled())
				logger.info("Using war file from location " + fileWar);
			wContext.setWar(fileWar);
			handlers.add(wContext);
		} else if(webapp.isWebAppRootGiven()) {
			WebAppContext wrContext = new WebAppContext();
			wrContext.setContextPath(contextPath);			
			String webappLocation;
			try {
				webappLocation = webapp.getResource().getFile().getAbsolutePath();
			} catch (IOException e) {
				throw new DeploymentException("Unable to get the URL to the resource, \"" + webapp.getResource().getFilename() + "\" ", e);
			}
			wrContext.setResourceBase(webappLocation);
			if(logger.isInfoEnabled())
				logger.info("Using webapp base direcctory  " + webappLocation);
			handlers.add(wrContext);
		}
		return true;
	}
}
