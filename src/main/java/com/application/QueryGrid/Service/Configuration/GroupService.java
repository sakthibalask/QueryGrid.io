package com.application.QueryGrid.Service.Configuration;

import com.application.QueryGrid.Entity.Group.GroupRoles;
import com.application.QueryGrid.Entity.Group.Groups;
import com.application.QueryGrid.Entity.UserAuth.User;
import com.application.QueryGrid.Repository.GroupRepository;
import com.application.QueryGrid.Repository.UserRepository;
import com.application.QueryGrid.Utils.PatchUtils;
import com.application.QueryGrid.dto.request.Groups.GroupPatchRequest;
import com.application.QueryGrid.dto.request.Groups.GroupRequest;
import com.application.QueryGrid.dto.response.Group.ReturnGroup;
import com.application.QueryGrid.dto.response.Group.ReturnGroups;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public Set<User> getSelectedUsers(Set<String> user_emails){
        Set<User> selectedUsers = new HashSet<>(Set.of());
        for(String user_email: user_emails){
            var user = userRepository.findByEmail(user_email).orElseThrow(() ->
                    new EntityNotFoundException("No user found for email: " + user_email)
            );
            selectedUsers.add(user);
        }
        return selectedUsers;
    }

    @Transactional
    public String createGroups(Principal connectedUser, GroupRequest groupRequest){
        var createdUser = ((User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal());
        Groups newgroup = Groups.builder()
                .groupName(groupRequest.getGroup_name())
                .description(groupRequest.getDescription())
                .isLocked(false)
                .createdBy(createdUser)
                .groupRole(
                        GroupRoles.valueOf(groupRequest.getGroupRole())
                )
                .users(getSelectedUsers(groupRequest.getUser_emails()))
                .build();
        groupRepository.save(newgroup);
        return "Group Created Successful";
    }


    @Transactional
    public String patchGroups(GroupPatchRequest groupPatchRequest){
        Groups group = groupRepository.findById(groupPatchRequest.getGroupName()).orElseThrow();

        if(groupPatchRequest.getGroupRole() != null){
            group.setGroupRole(GroupRoles.valueOf(groupPatchRequest.getGroupRole()));
        }

        if(groupPatchRequest.getUser_emails() != null){
            Set<User> members = getSelectedUsers(groupPatchRequest.getUser_emails());
            group.setUsers(members);
        }

        PatchUtils.copyNonNullProperties(groupPatchRequest, group);

        groupRepository.save(group);

        return "Group data Updated Successful";
    }

    public Set<String> getUserEmails(Set<User> users){
        Set<String> memberEmails = new HashSet<>();
        for(User user: users){
            memberEmails.add(user.getEmail());
        }

        return memberEmails;
    }

    public ReturnGroup getGroupDetails(
            String groupName
    ){
        Groups group = groupRepository.findById(groupName).orElseThrow();

        return ReturnGroup.builder()
                .group_name(groupName)
                .groupRole(group.getGroupName())
                .description(group.getDescription())
                .user_emails(getUserEmails(group.getUsers()))
                .build();
    }

    public ReturnGroups getAllGroupDetails(){
        Set<ReturnGroup> allGroups = new HashSet<>();
        List<Groups> groups = groupRepository.findAll();

        for(Groups group: groups){
            allGroups.add(getGroupDetails(group.getGroupName()));
        }

        return ReturnGroups.builder()
                .allGroups(allGroups)
                .build();
    }

    public List<String> getGroupNames() {
        List<String> groupNames = new ArrayList<>();
        List<Groups> groups = groupRepository.findAll();

        for(Groups group: groups){
            groupNames.add(group.getGroupName());
        }

        return groupNames;
    }


}
