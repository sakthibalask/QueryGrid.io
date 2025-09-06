package com.application.QueryGrid.io.dto.request.Groups;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupPatchRequest {
    private String groupName;
    private String description;
    private String groupRole;
    private boolean isLocked;
    private Set<String> user_emails;
}
