package com.application.QueryGrid.io.Service.Configuration;

import com.application.QueryGrid.io.Entity.UserAuth.User;
import com.application.QueryGrid.io.Entity.UserAuth.Role;
import com.application.QueryGrid.io.Entity.UserAuth.UserLicense.License;
import com.application.QueryGrid.io.Entity.UserAuth.UserLicense.LicenseType;
import com.application.QueryGrid.io.Repository.LicenseRepository;
import com.application.QueryGrid.io.Repository.TokenRepository;
import com.application.QueryGrid.io.Repository.UserRepository;
import com.application.QueryGrid.io.Utils.PatchUtils;
import com.application.QueryGrid.io.dto.request.LicenseAllocationRequest;
import com.application.QueryGrid.io.dto.request.UserPatchRequest;
import com.application.QueryGrid.io.dto.response.ReturnUser;
import com.application.QueryGrid.io.dto.response.ReturnUsers;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final LicenseRepository licenseRepository;

    @Transactional
    public String patchUser(UserPatchRequest userPatch){
        User user = userRepository.findByEmail(userPatch.getEmail()).orElseThrow();

        if(userPatch.getRole() != null){
            user.setRole(Role.valueOf(userPatch.getRole()));
        }

        if(userPatch.getPassword() != null){
            user.setPassword(passwordEncoder.encode(userPatch.getPassword()));

            userPatch.setPassword(null);
        }

        PatchUtils.copyNonNullProperties(userPatch, user);

        userRepository.save(user);

        return "User Details Updated Successful";
    }

    public ReturnUser getUser(String email){
        var user = userRepository.findByEmail(email).orElseThrow();
        return ReturnUser.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .login_name(user.getLogin_name())
                .isActive(user.getIsActive())
                .repositoryName(user.getRepositoryName())
                .role(user.getRole())
                .build();
    }

    public ReturnUsers getAllUsers(){
        var users = userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
        return ReturnUsers.builder()
                .users(users)
                .build();
    }

    private ReturnUser toDto(User user){
        return ReturnUser.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .login_name(user.getLogin_name())
                .isActive(user.getIsActive())
                .repositoryName(user.getRepositoryName())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public String deleteUser(String userEmail){
        if(userRepository.existsById(userEmail)){
            tokenRepository.deleteUserTokens(userEmail);
            userRepository.deleteById(userEmail);
            licenseRepository.deleteLicenseByEmail(userEmail);
            return "User deleted successful";
        }else{
            return "User not found";
        }
    }

    public String createLicense(){
        return UUID.randomUUID().toString();
    }

    @Transactional
    public String allocateLicense(Principal connectedUser, LicenseAllocationRequest allocationRequest){
        var assignedUser = userRepository.findByEmail(allocationRequest.getUserEmail()).orElseThrow();
        License license = License.builder()
                .license_name(allocationRequest.getLicenseName())
                .licenseType(LicenseType.valueOf(allocationRequest.getLicenseType()))
                .license_key(createLicense())
                .assignedUser(assignedUser)
                .issuedBy((User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal())
                .isExpired(false)
                .license_notes(allocationRequest.getLicense_notes())
                .build();
        licenseRepository.save(license);
        assignedUser.setLicensed(true);


        return "License Allocated Successful";
    }

}
