package cl.sdc.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IamServiceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApiApplication.class, args);
    }

}
