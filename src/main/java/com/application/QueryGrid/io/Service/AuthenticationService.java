package com.application.QueryGrid.io.Service;

import com.application.QueryGrid.io.Entity.Token.Token;
import com.application.QueryGrid.io.Entity.Token.TokenType;
import com.application.QueryGrid.io.Entity.UserAuth.User;
import com.application.QueryGrid.io.Entity.UserAuth.Role;
import com.application.QueryGrid.io.Repository.LicenseRepository;
import com.application.QueryGrid.io.Repository.TokenRepository;
import com.application.QueryGrid.io.Repository.UserRepository;
import com.application.QueryGrid.io.dto.request.LoginRequest;
import com.application.QueryGrid.io.dto.request.UserCreationRequest;
import com.application.QueryGrid.io.dto.response.LoginResponse;
import com.application.QueryGrid.io.dto.response.UserCreationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final LicenseRepository licenseRepository;

    public UserCreationResponse createUser(UserCreationRequest creationRequest){
        var user = User.builder()
                .email(creationRequest.getEmail())
                .username(creationRequest.getUsername())
                .login_name(creationRequest.getLogin_name())
                .password(passwordEncoder.encode(creationRequest.getPassword()))
                .repositoryName(creationRequest.getRepositoryName())
                .isActive(true)
                .role(Role.USER)
                .isLicensed(licenseRepository.existsByAssignedUserEmail(creationRequest.getEmail()))
                .build();
        if (userRepository.existsById(user.getEmail())){
            var response = "User exists already";
            return UserCreationResponse.builder().message(response).build();
        }
        userRepository.save(user);
        var response = "User Created Successfully";
        return UserCreationResponse.builder().message(response).build();
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest){
        var user = userRepository.findByLoginName(loginRequest.getLoginName()).orElseThrow();
        if(
                passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()) &&
                        (loginRequest.getRepositoryName()).equals(user.getRepositoryName()))
        {
            if((!user.isLicensed() && licenseRepository.getLicenseByEmail(user.getEmail()).isEmpty())){
                return LoginResponse.builder().token("No Token").message("Access denied: please verify your license status or contact your administrator.").build();
            }
            var userToken = jwtService.generateToken(user);
            var message = "User Authenticated Successfully";
            revokeAllUserTokens(user);
            savedUserToken(user, userToken);
            return LoginResponse.builder().token(userToken).message(message).build();

        }else{
            return LoginResponse.builder().token("No Token").message("Invalid credentials").build();
        }

    }

    private void revokeAllUserTokens(User user){
        var validUserToken = tokenRepository.findAllValidTokensByUser(user.getEmail());
        if(validUserToken.isEmpty()) {
            return;
        }
        validUserToken.forEach(t ->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }


    private void savedUserToken(User savedUser, String userToken){
        var token = Token.builder()
                .userInfo(savedUser)
                .token(userToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .createdDate(Date.from(
                        LocalDateTime.now()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                ))
                .build();
        tokenRepository.save(token);
    }

    public String logoutUser(Principal connectedUser){
        var user = ((User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal()).getEmail();
        var viewToken = tokenRepository.findByUser(user).orElse(null);
        if(viewToken != null) {
            viewToken.setExpired(true);
            viewToken.setRevoked(true);
            tokenRepository.save(viewToken);

        }
        return "Logged out Successfully";
    }
}
