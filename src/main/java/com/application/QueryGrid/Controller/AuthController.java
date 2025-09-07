package com.application.QueryGrid.Controller;

import com.application.QueryGrid.Service.AuthenticationService;
import com.application.QueryGrid.dto.request.UserAuth.LoginRequest;
import com.application.QueryGrid.dto.response.Auth.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/hello")
    public String getHello(){
        return "Hello";
    }
}
