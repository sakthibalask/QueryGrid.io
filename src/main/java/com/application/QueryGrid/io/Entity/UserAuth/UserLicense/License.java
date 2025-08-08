package com.application.QueryGrid.io.Entity.UserAuth.UserLicense;

import com.application.QueryGrid.io.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qg_licenses")
public class License {
    @Id
    private String license_name;

    @Column(length = 64, unique = true)
    private String license_key;

    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "license_user")
    private User assignedUser;

    @Column(nullable = false)
    private boolean isExpired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private User issuedBy;

    @Column(columnDefinition = "TEXT")
    private String license_notes;










}
