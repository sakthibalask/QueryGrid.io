package com.application.QueryGrid.io.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequest {
    private String group_name;
    private String description;
    private String groupRole;
    private Set<String> user_emails;
}
