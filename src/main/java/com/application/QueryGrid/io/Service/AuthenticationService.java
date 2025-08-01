package com.application.QueryGrid.io.Service;

import com.application.QueryGrid.io.Entity.Token.Token;
import com.application.QueryGrid.io.Entity.Token.TokenType;
import com.application.QueryGrid.io.Entity.User;
import com.application.QueryGrid.io.Entity.UserAuth.Role;
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

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public UserCreationResponse createUser(UserCreationRequest creationRequest){
        var user = User.builder()
                .email(creationRequest.getEmail())
                .username(creationRequest.getUsername())
                .login_name(creationRequest.getLogin_name())
                .password(passwordEncoder.encode(creationRequest.getPassword()))
                .repositoryName(creationRequest.getRepositoryName())
                .isActive(true)
                .role(Role.SUPERUSER)
                .build();
        userRepository.save(user);
        var response = "User Created Successfully";
        return UserCreationResponse.builder().message(response).build();
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest){
        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        if(
                passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()) &&
                        (loginRequest.getRepositoryName()).equals(user.getRepositoryName()))
        {
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
                .build();
        tokenRepository.save(token);
    }
}
