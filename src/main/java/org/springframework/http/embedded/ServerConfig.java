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
 * The common and the implementation specific configuration if any for the {@link EmbeddedServer} 
 * implementation
 * 
 * @author Amol Nayak
 *
 */
public class ServerConfig {

	private int startupPort = 8080;		//default one
	
	//TODO: Add support for SSL, config also should contain path to the certificate
	private boolean sslSupported;

	/**
	 * Gets the port on which the server listens to
	 * @return
	 */
	public int getStartupPort() {
		return startupPort;
	}

	/**
	 * Sets the port on which the server listens to
	 * @param startupPort
	 */
	public void setStartupPort(int startupPort) {
		this.startupPort = startupPort;
	}

	/**
	 * Indicates if SSL is to be used for the embedded server
	 * @return true if ssl is supported, else false. false by default
	 */
	public boolean isSslSupported() {
		return sslSupported;
	}

	public void setSslSupported(boolean sslSupported) {
		this.sslSupported = sslSupported;
	}	
}
