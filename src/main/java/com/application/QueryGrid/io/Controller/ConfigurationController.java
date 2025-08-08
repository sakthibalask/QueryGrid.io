package com.application.QueryGrid.io.Controller;

import com.application.QueryGrid.io.Service.AuthenticationService;
import com.application.QueryGrid.io.Service.Configuration.GroupService;
import com.application.QueryGrid.io.Service.Configuration.UserService;
import com.application.QueryGrid.io.dto.request.*;
import com.application.QueryGrid.io.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("app/v1/configuration")
@PreAuthorize("hasRole('SUPERUSER') or hasRole('ADMINISTRATOR')")
@RequiredArgsConstructor
public class ConfigurationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final GroupService groupService;


    /**
     * Configuration Endpoints for Users
     */

    @PostMapping("/createUser")
    @PreAuthorize("hasAuthority('superuser:CREATE') or hasAuthority('admin:CREATE')")
    public UserCreationResponse creationResponse(
            @RequestBody UserCreationRequest creationRequest
    ){
        return authenticationService.createUser(creationRequest);
    }

    @PatchMapping("/updateUser")
    @PreAuthorize("hasAuthority('superuser:PATCH') or hasAuthority('admin:PATCH')")
    public String updateUserDetails(
            @RequestBody UserPatchRequest patchRequest
    ){
        return userService.patchUser(patchRequest);
    }

    @GetMapping("/getUser")
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public ReturnUser returnUser(
            @RequestParam String email
    ){
        return userService.getUser(email);
    }

    @GetMapping("/getUsers")
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public ReturnUsers returnUsers(){
        return userService.getAllUsers();
    }

    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasAuthority('superuser:DELETE') or hasAuthority('admin:DELETE')")
    public String removeUser(
            @RequestParam String email
    ){
        return userService.deleteUser(email);
    }

    @PostMapping("/allocateLicense")
    @PreAuthorize("hasAuthority('superuser:CREATE') or hasAuthority('admin:CREATE')")
    public String allocateUserLicense(
            Principal connectedUser,
            @RequestBody LicenseAllocationRequest allocationRequest
    ){
        return userService.allocateLicense(connectedUser, allocationRequest);
    }



    /**
     * Configuration Endpoints for Groups
     */

    @PostMapping("/createGroup")
    @PreAuthorize("hasAuthority('superuser:CREATE') or hasAuthority('admin:CREATE')")
    public String groupCreation(
            @RequestBody GroupRequest groupRequest,
            Principal connectedUser
    ){
        return groupService.createGroups(connectedUser, groupRequest);
    }

    @PatchMapping("/updateGroup")
    @PreAuthorize("hasAuthority('superuser:UPDATE') or hasAuthority('admin:UPDATE')")
    public String updateGroup(
            @RequestBody GroupPatchRequest groupPatchRequest
    ){
        return groupService.patchGroups(groupPatchRequest);

    }

    @GetMapping("/getGroup")
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public ReturnGroup getGroup(
            @RequestParam String groupName
    ){
        return groupService.getGroupDetails(groupName);
    }

    @GetMapping("/getGroups")
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public ReturnGroups getGroups(){
        return groupService.getAllGroupDetails();
    }

}
