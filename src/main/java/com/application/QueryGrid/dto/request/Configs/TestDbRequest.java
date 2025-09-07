package com.application.QueryGrid.dto.request.Configs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestDbRequest {
    private String jdbcUrl;
    private String username;
    private String password;
}
