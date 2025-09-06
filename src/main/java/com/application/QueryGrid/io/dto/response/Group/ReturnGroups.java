package com.application.QueryGrid.io.dto.response.Group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnGroups {
    private Set<ReturnGroup> allGroups;
}
