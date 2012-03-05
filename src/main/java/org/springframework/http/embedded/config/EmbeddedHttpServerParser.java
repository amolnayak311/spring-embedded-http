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
package org.springframework.http.embedded.config;

import java.util.List;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.http.embedded.EmbeddedServerFactory;
import org.springframework.http.embedded.ServerConfig;
import org.springframework.http.embedded.ServletMapping;
import org.springframework.http.embedded.WebApplication;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;

import org.w3c.dom.Element;

/**
 * The parser for parsing the <it>embedded-server</it> node in the http name space 
 * @author Amol Nayak
 *
 */
public class EmbeddedHttpServerParser extends
		AbstractBeanDefinitionParser {

	private static final String TYPE= "type";
	private static final String CONFIG = "config";
	private static final String PORT_NUMBER_ATTRIBUTE = "port-number";
	private static final String PORT_NUMBER_PROPERTY = "startupPort";
	private static final String WEB_APP = "webapp";
	private static final String CONTEXT_PATH_ATTRIUTE = "context-path";
	private static final String CONTEXT_PATH = "contextPath";
	private static final String WAR_ELEMENT = "war";
	private static final String WEBAPP_DIR_ELEMENT = "webapp-dir";
	private static final String SERVLET_MAPPING_ELEMENT = "servlet-mapping";
	private static final String LOCATION_ATTRIBUTE = "location";
	private static final String WAR_PATH_PROPERTY = "warPath";
	private static final String WEB_APP_ROOT_PROPERTY = "webAppRoot";
	private static final String SERVLET_MAPPING_PROPERTY = "servletMappings";
	private static final String SERVLET_REF_ATTRIBUTE = "servlet-ref";
	private static final String URL_PATTERN_ATTRIBUTE = "url-pattern";
		
	@Override
	protected AbstractBeanDefinition parseInternal(Element element,
			ParserContext context) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(EmbeddedServerFactory.class);
		String type = element.getAttribute(TYPE);
		builder.addPropertyValue("config", getConfigBeanDefinition(element));
		builder.addPropertyValue("webApplications", getWebApplications(element));
		builder.addPropertyValue(TYPE, type);
		return builder.getBeanDefinition();
	}

	private AbstractBeanDefinition getConfigBeanDefinition(Element element) {
		Element node = DomUtils.getChildElementByTagName(element, CONFIG);		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ServerConfig.class);
		String portNumber = node.getAttribute(PORT_NUMBER_ATTRIBUTE);
			if(StringUtils.hasText(portNumber))
				builder.addPropertyValue(PORT_NUMBER_PROPERTY, Integer.valueOf(portNumber));
		//Other configs go here		
		return builder.getBeanDefinition();
	}
	
	private ManagedList<BeanDefinition> getWebApplications(Element element) {
		List<Element> webAppElements = DomUtils.getChildElementsByTagName(element, WEB_APP);		
		ManagedList<BeanDefinition> beanDefs = new ManagedList<BeanDefinition>();
		if(!webAppElements.isEmpty()) { //has to be present
			for(Element webAppElem:webAppElements) {				
				beanDefs.add(createWebApplicationDefinition(webAppElem));
			}
		} else {
			throw new BeanDefinitionStoreException("No web apps found to be deployed on the embedded http server");
		}			
		return beanDefs;
	}
	
	private BeanDefinition createWebApplicationDefinition(Element elem) {
		//Create the bean definition either for the war, webapp-dir or the servlet-mapping 
		String contextPath = elem.getAttribute(CONTEXT_PATH_ATTRIUTE);
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(WebApplication.class);
		builder.addPropertyValue(CONTEXT_PATH, contextPath);		
		//Only one of war, webapp-dir or servlet-mapping is supported as per xsd
		//Get the web app child element first
		Element warElement = DomUtils.getChildElementByTagName(elem, WAR_ELEMENT);		
		if(warElement != null) {			
			String warLocation = warElement.getAttribute(LOCATION_ATTRIBUTE);
			builder.addPropertyValue(WAR_PATH_PROPERTY, warLocation);
		} else {
			Element webAppDirElem = DomUtils.getChildElementByTagName(elem, WEBAPP_DIR_ELEMENT);			
			if(webAppDirElem != null) {				
				String webAppRoot = webAppDirElem.getAttribute(LOCATION_ATTRIBUTE);
				builder.addPropertyValue(WEB_APP_ROOT_PROPERTY, webAppRoot);
			} else {
				//Servlet mappings has to be present
				List<Element> servletMappings = DomUtils.getChildElementsByTagName(elem, SERVLET_MAPPING_ELEMENT);				
				ManagedList<BeanDefinition> servletMappingList = new ManagedList<BeanDefinition>();
				for(Element servletMapElem:servletMappings) {					
					BeanDefinitionBuilder servletMapping = BeanDefinitionBuilder.genericBeanDefinition(ServletMapping.class);
					String servletRef = servletMapElem.getAttribute(SERVLET_REF_ATTRIBUTE);
					String urlPattern = servletMapElem.getAttribute(URL_PATTERN_ATTRIBUTE);
					servletMapping.addConstructorArgReference(servletRef);
					servletMapping.addConstructorArgValue(urlPattern);
					servletMappingList.add(servletMapping.getBeanDefinition());
				}
				builder.addPropertyValue(SERVLET_MAPPING_PROPERTY, servletMappingList);
			}
		}
		return builder.getBeanDefinition();
	}

	@Override
	protected boolean shouldGenerateId() {		
		return true;
	}	
}

