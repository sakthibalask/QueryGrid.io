package com.application.QueryGrid.io.Service.Configuration;

import com.application.QueryGrid.io.Entity.Group.GroupRoles;
import com.application.QueryGrid.io.Entity.Group.Groups;
import com.application.QueryGrid.io.Entity.UserAuth.User;
import com.application.QueryGrid.io.Repository.GroupRepository;
import com.application.QueryGrid.io.Repository.UserRepository;
import com.application.QueryGrid.io.Utils.PatchUtils;
import com.application.QueryGrid.io.dto.request.GroupPatchRequest;
import com.application.QueryGrid.io.dto.request.GroupRequest;
import com.application.QueryGrid.io.dto.response.ReturnGroup;
import com.application.QueryGrid.io.dto.response.ReturnGroups;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
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

    public String createGroups(Principal connectedUser, GroupRequest groupRequest){
        var createdUser = ((User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal());
        Groups newgroup = Groups.builder()
                .group_name(groupRequest.getGroup_name())
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
                .groupRole(group.getGroup_name())
                .description(group.getDescription())
                .user_emails(getUserEmails(group.getUsers()))
                .build();
    }

    public ReturnGroups getAllGroupDetails(){
        Set<ReturnGroup> allGroups = new HashSet<>();
        List<Groups> groups = groupRepository.findAll();

        for(Groups group: groups){
            allGroups.add(getGroupDetails(group.getGroup_name()));
        }

        return ReturnGroups.builder()
                .allGroups(allGroups)
                .build();
    }


}
