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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * The common abstract class for all the embedded server implementations
 * @author Amol Nayak
 *
 */
public abstract class AbstractEmbeddedServer implements EmbeddedServer {

	protected final Log logger = LogFactory.getLog(getClass());
	
	protected ServerConfig config;
	private AtomicBoolean initialized = new AtomicBoolean(false);
	private AtomicBoolean started = new AtomicBoolean(false);
	private ServerRunStatus runStatus = ServerRunStatus.STOPPED;
	private ConcurrentHashMap<String,Object> deployedContexts = new ConcurrentHashMap<String, Object>();
	private static final Object STATIC_VALUE = new Object();
	
	/* (non-Javadoc)
	 * @see org.springframework.http.embedded.EmbeddedServer#initialize(org.springframework.http.embedded.ServerConfig)
	 */
	@Override
	public final void initialize(ServerConfig config) {
		if(logger.isDebugEnabled())
			logger.debug("Initializing the server configuration, configuring to start on port " + config.getStartupPort());
		
		if(initialized.compareAndSet(false, true)) {
			this.config = config; 
		} else {			
			logger.warn("Cannot initialize as the server is already initialized");
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.embedded.EmbeddedServer#start()
	 */
	@Override
	public final void start() {
		if(config == null)
			throw new IllegalStateException("Cannot start an uninitialized server");
		if(logger.isDebugEnabled())
			logger.debug("Starting the server");
		
		if(started.compareAndSet(false, true)) {
			runStatus = ServerRunStatus.STARTING;
			try {
				doStart();
			} catch (Exception e) {
				logger.error("Exception occurred while starting the embedded server",e);
				runStatus = ServerRunStatus.FAILED;
				return;
			}
			runStatus = ServerRunStatus.RUNNING;
		} else {
			if(logger.isInfoEnabled())
				logger.info("Start of the server already triggered, current status of the server is " + getStatus());
		}
	}
	
	/**
	 * The subclass class needs to implement the method to start the server in embedded mode
	 * The sub class should throw an exception should it face to start the server.
	 * The implementing class should NOT spawn a new thread and start the server. The
	 * starting of the server should happen in the same thread   
	 */
	protected abstract void doStart() throws Exception;	

	/* (non-Javadoc)
	 * @see org.springframework.http.embedded.EmbeddedServer#stop()
	 */
	@Override
	public final void stop() {
		if(logger.isDebugEnabled())
			logger.debug("Stopping the server");
		
		if(started.compareAndSet(true, false)) {
			runStatus = ServerRunStatus.STOPPING;
			try {
				doStop();
			} catch (Exception e) {
				logger.error("Caught Exception while stopping the server",e);
				runStatus = ServerRunStatus.UNKNOWN;
				return;
			}
			runStatus = ServerRunStatus.STOPPED;
		} else {
			logger.info("Server not started or in process of stopping, server run status is \"" + runStatus + "\"");
		}
	}
	
	/**
	 * The method that needs to be implemented by the concrete subclass to stop the embedded server
	 * The stopping of the server should happen in the same thread and the implementing class should not spawn a new thread
	 * @throws Exception
	 */
	protected abstract void doStop() throws Exception;

	/* (non-Javadoc)
	 * @see org.springframework.http.embedded.EmbeddedServer#deployApplication(java.lang.String, org.springframework.http.embedded.WebApplication)
	 */
	@Override
	public final void deployApplication(WebApplication webapp) {
		String contextPath = webapp.getContextPath();
		if(logger.isDebugEnabled())
			logger.debug("Attempting to deploy web application on the context path \"" +  contextPath + "\"");
		
		if(deployedContexts.putIfAbsent(contextPath, STATIC_VALUE) == null) {
			boolean appDeploymentStatus;
			try {
				appDeploymentStatus = doDeployApplication(webapp);
				if(appDeploymentStatus)
					return;
			} catch (Exception e) {
				logger.error("Caught exception while deploying web application with context path \"" + contextPath + "\"");
				appDeploymentStatus = false;
			}
			if(!appDeploymentStatus) {
				logger.info("App deployment failed for context \"" + contextPath + "\"");
				deployedContexts.remove(contextPath,STATIC_VALUE);
				return;
				// to enable redeployment with this context path
			}				
		}		 
		throw new DuplicateContextPathException(contextPath);
	}
	
	/**
	 * The method to be implemented by the subclass to deploy the provided web application on the embedded server
	 * @return 	boolean flag indicating the sub class successfully deployed the application to the embedded server, true if 
	 *			true if successful, else false
	 * @throws Exception
	 */
	protected abstract boolean doDeployApplication(WebApplication webapp);

	/**
	 * Gets the current run status of the application server
	 */
	@Override	
	public final ServerRunStatus getStatus() {		
		return runStatus;
	}
}
