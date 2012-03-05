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

import javax.servlet.http.HttpServlet;

import org.springframework.util.Assert;

/**
 * The class that holds the mapping of the Servlet instance to its path within an application
 * @author Amol Nayak
 *
 */
public class ServletMapping {

	private HttpServlet servlet;
	private String urlPattern;	
	
	public ServletMapping(HttpServlet servlet, String urlPattern) {
		Assert.hasText(urlPattern);
		Assert.notNull(servlet);
		this.servlet = servlet;
		this.urlPattern = urlPattern;
	}
	/**
	 * Gets the Servlet instance that needs to be added to the web app
	 * @return
	 */
	public HttpServlet getServlet() {
		return servlet;
	}	
	
	/**
	 * Gets the URL pattern for the servlet
	 * @return
	 */
	public String getUrlPattern() {
		return urlPattern;
	}
	
}
