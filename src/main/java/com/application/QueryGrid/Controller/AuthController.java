package com.application.QueryGrid.Controller;

import com.application.QueryGrid.Service.AuthenticationService;
import com.application.QueryGrid.dto.request.UserAuth.LoginRequest;
import com.application.QueryGrid.dto.response.Auth.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("app/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;

    @PostMapping(value = "/authenticateUser/config", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse loginResponseConfig(
            @RequestBody LoginRequest loginRequest
    ){
        return authService.authenticateUser(loginRequest, "config");
    }

    @PostMapping(value = "/authenticateUser/client", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse loginResponseClient(
            @RequestBody LoginRequest loginRequest
    ){
        return authService.authenticateUser(loginRequest, "client");
    }

    @GetMapping(value = "/checkValid")
    public ResponseEntity<Boolean> checkToken(@RequestParam String token) {
        boolean valid = authService.istokenValid(token);
        return ResponseEntity.ok(valid);
    }

    @GetMapping("/test-connection")
    public String getHello(){
        return "TestConnection successful";
    }
}
