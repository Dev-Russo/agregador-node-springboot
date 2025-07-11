package br.ufv.sin142.aggregadornode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AggregatorNodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregatorNodeApplication.class, args);
    }

}