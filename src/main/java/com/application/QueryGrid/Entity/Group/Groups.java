package com.application.QueryGrid.Entity.Group;

import com.application.QueryGrid.Entity.Configs.DatabaseConfigs;
import com.application.QueryGrid.Entity.UserAuth.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qg_user_groups")
public class Groups {
    @Id
    @Column(name = "group_name", nullable = false, unique = true)
    private String groupName;

    @Column(length = 1024)
    private String description;

    @ManyToOne
    @JoinColumn(name = "group_owner")
    private User createdBy;

    @Column(nullable = false)
    private boolean isLocked;

    @Enumerated(EnumType.STRING)
    private GroupRoles groupRole;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "group_name")
    private Set<User> users;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<DatabaseConfigs> db_configs;


}
