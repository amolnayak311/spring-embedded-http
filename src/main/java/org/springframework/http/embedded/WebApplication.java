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
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * The class represents a web application that needs to be deployed on the {@link EmbeddedServer}.
 * The web application will be contained in a war, some absolute location on the file system
 * or some class path resources 
 *  
 * @author Amol Nayak
 *
 */
public class WebApplication implements InitializingBean {
	
	private List<ServletMapping> servletMappings;
	
	private String warPath;
	
	private String webAppRoot;
	
	private Resource resource;
	
	private boolean warPathGiven;
	
	private boolean servletMappingGiven;
	
	private boolean webAppRootGiven;
	
	private String contextPath;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(!StringUtils.hasText(contextPath))
			new IllegalArgumentException("Setting a non null, non empty context path is mandatory");
		
		servletMappingGiven = servletMappings != null && !servletMappings.isEmpty();
		warPathGiven = StringUtils.hasText(warPath);
		webAppRootGiven = StringUtils.hasText(webAppRoot);
		
		int count = 0;
		if(servletMappingGiven)
			count++;
		if(warPathGiven)
			count++;
		if(webAppRootGiven)
			count++;
		
		if(count > 1)
			throw new IllegalArgumentException("Exactly one of servlet mappings,war path or web app root is required");
		if(count == 0)
			throw new IllegalArgumentException("At least one of servlet mappings,war path or web app root is required");
		
		if(warPathGiven) {
			String lowerCaseName = warPath.toLowerCase();
			if(lowerCaseName.startsWith("classpath:") || lowerCaseName.startsWith("classpath*:")) {
				//This is a classpath resource
				resource = new ClassPathResource(warPath.substring(warPath.indexOf(":") + 1));
				File file = resource.getFile();
				if (!file.isFile()) {
					throw new DeploymentException("File \"" + file.getAbsolutePath() + "\" is not a regular file");
				}
			} else {
				//Assuming this is a file system path
				File file = new File(warPath);
				if(!file.exists())
					throw new IllegalArgumentException("The resource \"" + warPath + "\" does not exist");
				
				if (!file.isFile()) {
					throw new DeploymentException("File \"" + file.getAbsolutePath() + "\" is not a regular file");
				}
				resource = new FileSystemResource(file);
			}			
		}
		if(webAppRootGiven) {
			File root = new File(webAppRoot);
			if(!root.exists())
				throw new IllegalArgumentException("The resource \"" + warPath + "\" does not exist");
			if(!root.isDirectory()) 
				throw new IllegalArgumentException("The resource \"" + warPath + "\" is not a directory");
			resource = new FileSystemResource(root);
		}
	}

	/**
	 * Gets the list of servlet mappings for the given web application.
	 * @return
	 */
	public List<ServletMapping> getServletMappings() {
		return servletMappings;
	}

	public void setServletMappings(List<ServletMapping> servletMappings) {
		this.servletMappings = servletMappings;
	}

	/**
	 * Gets the path for the war file that needs to be deployed on the server
	 * This path can be for  a classpath or be an absolute path on the file system   
	 * @return
	 */
	public String getWarPath() {
		return warPath;
	}

	public void setWarPath(String warPath) {
		this.warPath = warPath;
	}
	
	/**
	 * The root directory of the web app	
	 * @param webAppRoot
	 */
	public void setWebAppRoot(String webAppRoot) {
		this.webAppRoot = webAppRoot;
	}

	/**
	 * Gets the resource corresponding to the war file, returns null if
	 * {@link ServletMapping} used for the web application
	 * @return
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Returns true if a war file path is given 
	 * @return
	 */
	public boolean isWarPathGiven() {
		return warPathGiven;
	}

	/**
	 * Returns true if servlet mapping are given 
	 * @return
	 */
	public boolean isServletMappingGiven() {
		return servletMappingGiven;
	}

	/**
	 * Returns true if a folder representing the webapp root is given 
	 * @return
	 */
	public boolean isWebAppRootGiven() {
		return webAppRootGiven;
	}

	/**
	 * The Context path for this application
	 * @return
	 */
	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
}