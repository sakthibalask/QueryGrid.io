package com.application.QueryGrid.io.Controller;

import com.application.QueryGrid.io.Service.AuthenticationService;
import com.application.QueryGrid.io.dto.request.UserAuth.LoginRequest;
import com.application.QueryGrid.io.dto.response.Auth.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;

    @PostMapping(value = "/authenticateUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse loginResponse(
            @RequestBody LoginRequest loginRequest
    ){
        return authService.authenticateUser(loginRequest);
    }
}
