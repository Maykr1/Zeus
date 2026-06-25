package io.github.maykr1.zeus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZeusApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeusApplication.class, args);
	}

}
