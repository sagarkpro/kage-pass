package com.sagar.kagepass;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KagepassApplication {

	public static void main(String[] args) {
		SpringApplication.run(KagepassApplication.class, args);

		System.out.println("\n\n\n\n\n<---------------APPLICATION STARTED------------------>\n\n\n\n\n");
		
		// this code opens the given url in default browser
		try{
			System.setProperty("java.awt.headless", "false"); // to get rid of headless exception
			java.awt.Desktop.getDesktop().browse(new URI("http://localhost:8080/swagger-ui/index.html"));
		}
		catch(Exception ex){
			System.out.println(ex);
		}
	}

}
