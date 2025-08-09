package com.application.QueryGrid.io;

import com.application.QueryGrid.io.Entity.UserAuth.User;
import com.application.QueryGrid.io.Entity.UserAuth.Role;
import com.application.QueryGrid.io.Entity.UserAuth.UserLicense.License;
import com.application.QueryGrid.io.Entity.UserAuth.UserLicense.LicenseType;
import com.application.QueryGrid.io.Repository.LicenseRepository;
import com.application.QueryGrid.io.Repository.UserRepository;
import com.application.QueryGrid.io.Service.Configuration.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final LicenseRepository licenseRepository;
	private final UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Bean
	public ApplicationRunner createAdminByDefault(){
		return args -> {
			userRepository.findByEmail("admin@qg.com")
					.orElseGet(() -> {
						User adminUser = User.builder()
								.email("admin@qg.com")
								.username("Administrator")
								.login_name("Administrator")
								.repositoryName("adminRepo")
								.isActive(true)
								.password(passwordEncoder.encode("Password@123"))
								.role(Role.ADMINISTRATOR)
								.isLicensed(true)
								.build();
						return userRepository.save(adminUser);
					});

			licenseRepository.findById("adminLicense")
					.orElseGet(() -> {
						var adminUser = userRepository.findByEmail("admin@qg.com").orElseThrow();
						License adminLicense = License.builder()
								.license_name("adminLicense")
								.license_key(userService.createLicense())
								.licenseType(LicenseType.QG_X3)
								.issuedBy(adminUser)
								.assignedUser(adminUser)
								.license_notes("License Created for Administrator Use only")
								.isExpired(false)
								.build();
						return licenseRepository.save(adminLicense);
					});
		};
	}

}
