package com.application.QueryGrid.io.Controller;

import com.application.QueryGrid.io.Service.AuthenticationService;
import com.application.QueryGrid.io.dto.request.LoginRequest;
import com.application.QueryGrid.io.dto.request.UserCreationRequest;
import com.application.QueryGrid.io.dto.response.LoginResponse;
import com.application.QueryGrid.io.dto.response.UserCreationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;

    @PostMapping("/authenticateUser")
    public LoginResponse loginResponse(
            @RequestBody LoginRequest loginRequest
    ){
        return authService.authenticateUser(loginRequest);
    }
}
