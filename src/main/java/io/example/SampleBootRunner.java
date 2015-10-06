package io.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@ComponentScan
@EnableJpaRepositories
public class SampleBootRunner {

	public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleBootRunner.class, args);
    }

}