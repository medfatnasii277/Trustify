package com.trustify.policy_service;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Optional;

/**
 * Main application class for the Policy Service
 */
@SpringBootApplication
public class PolicyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyServiceApplication.class, args);
	}
	
	/**
	 * Application startup logger
	 *
	 * @param env the Spring environment
	 * @param buildProperties the application build properties (optional)
	 * @return the application runner
	 */
	@Bean
	public ApplicationRunner applicationRunner(Environment env, Optional<BuildProperties> buildProperties) {
		return args -> {
			String protocol = env.getProperty("server.ssl.key-store") != null ? "https" : "http";
			String serverPort = env.getProperty("server.port", "8081");
			String contextPath = env.getProperty("server.servlet.context-path", "/");
			if (!contextPath.startsWith("/")) {
				contextPath = "/" + contextPath;
			}
			if (!contextPath.endsWith("/")) {
				contextPath += "/";
			}
			
			String hostAddress = "localhost";
			try {
				hostAddress = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception e) {
				System.out.println("Error getting host address: " + e.getMessage());
			}
			
			System.out.println("\n----------------------------------------------------------");
			System.out.println("  Policy Service is running!");
			System.out.println("----------------------------------------------------------");
			
			System.out.println("  Application is running at:");
			System.out.println("  - Local:   " + protocol + "://localhost:" + serverPort + contextPath);
			System.out.println("  - Network: " + protocol + "://" + hostAddress + ":" + serverPort + contextPath);
			
			System.out.println("  Profile(s): " + Arrays.toString(env.getActiveProfiles()));
			
			buildProperties.ifPresent(props -> 
				System.out.println("  Version:    " + props.getVersion() + " (build: " + props.getTime() + ")"));
				
			System.out.println("  API Docs:   " + protocol + "://localhost:" + serverPort + "/swagger-ui.html");
			System.out.println("  H2 Console: " + protocol + "://localhost:" + serverPort + "/h2-console");
			
			System.out.println("----------------------------------------------------------");
		};
	}
}
