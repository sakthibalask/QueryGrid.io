package com.application.QueryGrid.io.Controller;

import com.application.QueryGrid.io.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("app/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;

    @PostMapping("/logout")
    public String userLogout(Principal connectedUser){
        return authenticationService.logoutUser(connectedUser);
    }

}
