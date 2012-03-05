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

/**
 * The interface for providing the abstract, core functionality the implementing embedded server should
 * provide
 * 
 * @author Amol Nayak
 *
 */
public interface EmbeddedServer {

	/**
	 * Invoked to initialize the server with the provided {@link ServerConfig}.
	 * 
	 * @param 	config the server configuration to configure the server with
	 * @throws 	IllegalStateException if invoked after server is started
	 */
	void initialize(ServerConfig config);
	
	/**
	 * Starts the server with the initialized configuration, attempt to start a started server will be 
	 * ignored 
	 * 
	 * @throws 	IllegalStateException if attempted to start an uninitialized server 
	 */
	void start();
	
	/**
	 * Stops a started server, attempt to stop a stopped server will be ignored
	 */
	void stop();
	
	/**
	 * Deploy a web application to the embedded server on the provided context path and 
	 * with the web application details provided.  
	 * 
	 * 
	 * @param 	webapp an instance of {@link WebApplication} that contains the details 
	 * 			about the web application to be deployed
	 * @throws 	IllegalStateException if invoked when the server is started 
	 * @throws 	DuplicateContextPathException if the passed <em>contextPath</em> is already registered with
	 * 			the server 
	 */
	void deployApplication(WebApplication webapp);
	
	/**
	 * Gets the status the server is in currently
	 * @return
	 */
	ServerRunStatus getStatus();
}
