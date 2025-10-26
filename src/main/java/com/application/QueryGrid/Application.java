package com.application.QueryGrid;

import com.application.QueryGrid.Entity.Group.GroupRoles;
import com.application.QueryGrid.Entity.Group.Groups;
import com.application.QueryGrid.Entity.UserAuth.User;
import com.application.QueryGrid.Entity.UserAuth.Role;
import com.application.QueryGrid.Entity.UserAuth.UserLicense.License;
import com.application.QueryGrid.Entity.UserAuth.UserLicense.LicenseType;
import com.application.QueryGrid.Repository.GroupRepository;
import com.application.QueryGrid.Repository.LicenseRepository;
import com.application.QueryGrid.Repository.UserRepository;
import com.application.QueryGrid.Service.Configuration.GroupService;
import com.application.QueryGrid.Service.Configuration.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final LicenseRepository licenseRepository;
    private final GroupRepository groupRepository;
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

            groupRepository.findById("adminGroup")
                            .orElseGet(() -> {
                                var adminUser = userRepository.findByEmail("admin@qg.com").orElseThrow();
                                Groups adminGroup = Groups.builder()
                                        .groupName("adminGroup")
                                        .description("A Default Admin Group")
                                        .groupRole(GroupRoles.EXECUTOR)
                                        .createdBy(adminUser)
                                        .users(Set.of(adminUser))
                                        .build();
                                return groupRepository.save(adminGroup);
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
