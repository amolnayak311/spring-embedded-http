Spring Embedded HTTP Sandbox
================================

### Introduction

This project intends to provide the namespace support for embedded http servers like JETTY and TOMCAT.
Currently applications requiring to use embedded http servers need to be aware of the API of the 
implementation they plan to use and write code for instantiating, initializing, starting and stopping 
the server. By providing the namespace support we simply provide the configuration and web application
details in the config file and spring will take care to do the heavy lifting of instantiating, configuring,
starting and stopping the server.

###Sample config

	<http:embedded-server>
		<http:config port-number="8085"/>
				
		<http:webapp context-path="/testservletmap">
			<http:servlet-mapping servlet-ref="helloServlet" url-pattern="/hello"/>
		</http:webapp>
		
		<http:webapp context-path="/testwar">
			<http:war location="classpath:testapp.war"/>
		</http:webapp>
		
		<http:webapp context-path="/testwebappdir">
			<http:webapp-dir location="src/test/resources/testwebapproot"/>
		</http:webapp>		
	</http:embedded-server>

You can find a couple of test cases under src/test/java source folder.

###Whats planned next?

* Support Tomcat, currently only Jetty implementation is supported
* Accept implementation specific configurations
* Enable SSL support
* Enable JMX support to start/stop server and view server status.


I have raised a JIRA https://jira.springsource.org/browse/SPR-9196 for this enhancements.
Please post your feedback, suggestions against this JIRA. 
 