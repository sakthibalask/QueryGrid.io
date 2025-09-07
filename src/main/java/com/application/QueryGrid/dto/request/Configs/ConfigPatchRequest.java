package com.application.QueryGrid.dto.request.Configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigPatchRequest {
    private String configName;
    private String dbType;
    private String host;
    private Integer port;
    private String databaseName;
    private String username;
    private String password;
    private String configType;
    private Set<String> groupNames;
}
