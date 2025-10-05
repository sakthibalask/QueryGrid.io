package com.application.QueryGrid.dto.response.Configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnConfigNames {
    private String configName;
    private List<String> groupNames;

}
