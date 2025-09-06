package com.application.QueryGrid.io.dto.request.UserAuth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LicenseAllocationRequest {
    private String licenseName;
    private String userEmail;
    private String licenseType;
    private String license_notes;
}
