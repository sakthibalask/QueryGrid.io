package com.application.QueryGrid.Controller;

import com.application.QueryGrid.Service.AuthenticationService;
import com.application.QueryGrid.Service.Configuration.ConfigService;
import com.application.QueryGrid.dto.response.Configs.ReturnConfigs;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("app/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;
    private final ConfigService configService;

    @PostMapping("/logout")
    public String userLogout(Principal connectedUser){
        return authenticationService.logoutUser(connectedUser);
    }

    @GetMapping("/getAccessConfigs")
    @PreAuthorize("hasRole('USER')")
    public ReturnConfigs getAccessedConfigs(Principal connectedUser) throws  Exception{
        return configService.getConfigsByGroupName(connectedUser);
    }



}
