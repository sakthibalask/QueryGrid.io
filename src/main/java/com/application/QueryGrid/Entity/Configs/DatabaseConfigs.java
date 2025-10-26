package com.application.QueryGrid.Entity.Configs;

import com.application.QueryGrid.Entity.Group.Groups;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qg_database_configs")
public class DatabaseConfigs {
    @Id
    @Column(name = "config_name", nullable = false, unique = true)
    private String configName;

    @Column(name = "db_type", length = 20, nullable = false)
    private String dbType;

    @Column(length = 255, nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer port;

    @Column(name = "database_name", length = 100, nullable = false)
    private String databaseName;

    @Column(length = 100, nullable = false)
    private String username;

    @Column(name = "password", columnDefinition = "text", nullable = false)
    private String password;

    @Column(name = "config_type")
    @Enumerated(EnumType.STRING)
    private ConfigTypes configTypes;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "config_groups",
            joinColumns = @JoinColumn(name = "configName", referencedColumnName = "config_name"),
            inverseJoinColumns = @JoinColumn(name = "group_name", referencedColumnName = "group_name")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Groups> groups;

    @Column(name = "granted_records", columnDefinition = "text", nullable = true)
    private String grantedRecords;

}
