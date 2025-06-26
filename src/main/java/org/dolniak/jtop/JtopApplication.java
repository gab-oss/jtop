package org.dolniak.jtop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class JtopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JtopApplication.class, args);
	}
}
