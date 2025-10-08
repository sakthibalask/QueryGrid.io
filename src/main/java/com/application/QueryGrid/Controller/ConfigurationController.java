package com.application.QueryGrid.Controller;

import com.application.QueryGrid.Service.AuthenticationService;
import com.application.QueryGrid.Service.Configuration.ConfigService;
import com.application.QueryGrid.Service.Configuration.GroupService;
import com.application.QueryGrid.Service.Configuration.UserService;
import com.application.QueryGrid.dto.request.Configs.ConfigCreateRequest;
import com.application.QueryGrid.dto.request.Configs.ConfigPatchRequest;
import com.application.QueryGrid.dto.request.Groups.GroupPatchRequest;
import com.application.QueryGrid.dto.request.Groups.GroupRequest;
import com.application.QueryGrid.dto.request.UserAuth.LicenseAllocationRequest;
import com.application.QueryGrid.dto.request.UserAuth.UserCreationRequest;
import com.application.QueryGrid.dto.request.UserAuth.UserPatchRequest;
import com.application.QueryGrid.dto.response.Configs.ReturnConfig;
import com.application.QueryGrid.dto.response.Configs.ReturnConfigNames;
import com.application.QueryGrid.dto.response.Configs.ReturnConfigs;
import com.application.QueryGrid.dto.response.Group.ReturnGroup;
import com.application.QueryGrid.dto.response.Group.ReturnGroups;
import com.application.QueryGrid.dto.response.User.ReturnUser;
import com.application.QueryGrid.dto.response.User.ReturnUsers;
import com.application.QueryGrid.dto.response.User.UserCreationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("app/v1/configuration")
@PreAuthorize("hasRole('SUPERUSER') or hasRole('ADMINISTRATOR')")
@RequiredArgsConstructor
public class ConfigurationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final GroupService groupService;
    private final ConfigService configService;


    /**
     * Configuration Endpoints for Users
     */

    @PostMapping(value = "/createUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('superuser:CREATE') or hasAuthority('admin:CREATE')")
    public UserCreationResponse creationResponse(
            @RequestBody UserCreationRequest creationRequest
    ){
        return authenticationService.createUser(creationRequest);
    }

    @PatchMapping(value = "/updateUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('superuser:PATCH') or hasAuthority('admin:PATCH')")
    public String updateUserDetails(
            @RequestBody UserPatchRequest patchRequest
    ){
        return userService.patchUser(patchRequest);
    }

    @GetMapping(value = "/getUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public ReturnUser returnUser(
            @RequestParam String email
    ){
        return userService.getUser(email);
    }

    @GetMapping(value = "/getUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public ReturnUsers returnUsers(){
        return userService.getAllUsers();
    }

    @DeleteMapping(value = "/deleteUser", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping("/getGroupNames")
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public List<String> fetchGroupNames(){
        return groupService.getGroupNames();
    }

    /*
      Configuration Endpoints for Database configuration.
     */

    @PostMapping("/createConfigs")
    @PreAuthorize("hasAuthority('superuser:CREATE') or hasAuthority('admin:CREATE')")
    public String saveConfig(
            @RequestBody ConfigCreateRequest configRequest
    ) throws Exception {
        return configService.createConfig(configRequest);
    }

    @GetMapping("/getConfigDetails")
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public ReturnConfig getConfigDetails(
            @RequestParam String configName
    )
    throws  Exception{
        return configService.getConfig(configName);
    }

    @GetMapping("/getConfigsName")
    @PreAuthorize("hasAuthority('superuser:READ') or hasAuthority('admin:READ')")
    public Set<ReturnConfigNames> getConfigNames(){
        return configService.getConfigNames();
    }


    @PostMapping(value = "/upload/config", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('superuser:CREATE') or hasAuthority('admin:CREATE')")
    public ReturnConfigs uploadConfig(@RequestParam("file")MultipartFile file)throws Exception {
        if(file == null || file.isEmpty()){
            throw new Exception("File is missing or empty");
        }

        return configService.importConfiguration(file);
    }

    @PatchMapping("/update/config")
    @PreAuthorize("hasAuthority('superuser:UPDATE') or hasAuthority('admin:UPDATE')")
    public String updateConfig(
            @RequestBody ConfigPatchRequest patchRequest
    ) throws Exception {
     return configService.patchConfig(patchRequest);
    }

    @DeleteMapping("/delete/config")
    @PreAuthorize("hasAuthority('superuser:DELETE') or hasAuthority('admin:DELETE')")
    public String trashConfig(
            @RequestParam String name
    ) throws Exception{
        return configService.deleteConfig(name);
    }

    @PostMapping("/save/config")
    @PreAuthorize("hasAuthority('superuser:CREATE') or hasAuthority('admin:CREATE')")
    public String saveConfig(
            @RequestBody ReturnConfigs previewConfigs
    ) throws  Exception{
        try{
            return configService.saveConfiguration(previewConfigs);
        }catch (Exception e){
            throw new Exception("Uploaded configuration cannot be saved due to "+ e.getMessage(), e);
        }
    }

    @PostMapping(value = "/export/config", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ByteArrayResource> exportConfig(
            @RequestBody ReturnConfigs dto,
            Principal connectedUser
    ){
        try{
            byte[] xmlBytes = configService.exportConfiguration(dto);
            ByteArrayResource resource = new ByteArrayResource(xmlBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setContentLength(xmlBytes.length);
            headers.setContentDisposition(ContentDisposition.attachment().filename(connectedUser.getName() + ".xml").build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
