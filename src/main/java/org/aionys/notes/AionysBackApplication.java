package org.aionys.notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AionysBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AionysBackApplication.class, args);
    }

}
