package com.neech.source;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.neech.source.repository")
@SpringBootApplication
public class MusicEntriesWebApp {

	public static void main(String[] args) {
		SpringApplication.run(MusicEntriesWebApp.class, args);
	}

}
