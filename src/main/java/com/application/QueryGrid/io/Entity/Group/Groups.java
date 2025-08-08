package com.application.QueryGrid.io.Entity.Group;

import com.application.QueryGrid.io.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class Groups {
    @Id
    @Column(nullable = false, unique = true)
    private String group_name;

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

}
