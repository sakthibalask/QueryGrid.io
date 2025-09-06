package com.application.QueryGrid.io.Repository;

import com.application.QueryGrid.io.Entity.Configs.DatabaseConfigs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ConfigsRepository extends JpaRepository<DatabaseConfigs, String> {
    Optional<DatabaseConfigs> findByConfigName(String configName);
    boolean existsByConfigName(String configName);

    Set<DatabaseConfigs> findAllByGroups_GroupName(String groupName);
}
