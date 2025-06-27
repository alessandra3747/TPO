package pjatk.tpo.tpo6_fa_s30395;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pjatk.tpo.tpo6_fa_s30395.model.Horse;
import pjatk.tpo.tpo6_fa_s30395.model.User;
import pjatk.tpo.tpo6_fa_s30395.repository.HorseRepository;
import pjatk.tpo.tpo6_fa_s30395.repository.UserRepository;

@SpringBootApplication
public class Tpo6FaS30395Application {

    public static void main(String[] args) {
        SpringApplication.run(Tpo6FaS30395Application.class, args);
    }

    @Bean
    CommandLineRunner runner(HorseRepository horseRepo, UserRepository userRepo) {
        return args -> {};
    }

}
