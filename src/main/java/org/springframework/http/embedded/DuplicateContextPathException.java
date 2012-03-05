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
 * Thrown from the <em>deployApplication</em> method of the {@link EmbeddedServer} if the 
 * context path provided is already registered with the server
 *   
 * @author Amol Nayak
 *
 */
public class DuplicateContextPathException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Represents the context path that failed as it was already registered with the server
	 */
	private String contextPath;

	/**
	 * The constructor that accepts the context path that failed to register
	 * @param contextPath
	 */
	public DuplicateContextPathException(String contextPath) {	
		this.contextPath = contextPath;
	}

	/**
	 * Gets the context path
	 * @return
	 */
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public String getMessage() {		
		return "\"" + contextPath + "\" failed to be added as an application " +
				"with this context path is already registered";
	}
}
