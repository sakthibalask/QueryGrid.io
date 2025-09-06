package com.application.QueryGrid.io.dto.response.User;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnUsers {
    private List<ReturnUser> users;
}
