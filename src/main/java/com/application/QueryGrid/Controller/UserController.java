package com.application.QueryGrid.Controller;

import com.application.QueryGrid.Service.AuthenticationService;
import com.application.QueryGrid.Service.Configuration.ConfigService;
import com.application.QueryGrid.dto.request.Configs.RunQueryRequest;
import com.application.QueryGrid.dto.response.Configs.ReturnConfigs;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
    public ReturnConfigs getAccessedConfigs(Principal connectedUser) throws  Exception{
        return configService.getConfigsByGroupName(connectedUser);
    }

    @PostMapping("/executeQuery")
    @PreAuthorize("hasRole('USER')")
    public List<Map<String, Object>> getTables(
            @RequestBody RunQueryRequest queryRequest
            ) throws SQLException {
        return configService.runQuery(queryRequest);
    }



}
