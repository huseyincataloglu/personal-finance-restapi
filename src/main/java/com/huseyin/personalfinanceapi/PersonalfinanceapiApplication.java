package com.huseyin.personalfinanceapi;

import com.huseyin.personalfinanceapi.user.entity.Roles;
import com.huseyin.personalfinanceapi.user.repository.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling
public class   PersonalfinanceapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalfinanceapiApplication.class, args);
	}

	@Bean
	public CommandLineRunner roleUploader(
			RolesRepository rolesRepository
	){
		return (args) -> {
			if (!rolesRepository.existsByName(Roles.UserRole.USER)) {
				Roles userRole = new Roles();
				userRole.setName(Roles.UserRole.USER);

				rolesRepository.save(userRole);
			}
			if (!rolesRepository.existsByName(Roles.UserRole.ADMIN)) {
				Roles adminRole = new Roles();
				adminRole.setName(Roles.UserRole.ADMIN);
				rolesRepository.save(adminRole);
			}

		};
	}

}
