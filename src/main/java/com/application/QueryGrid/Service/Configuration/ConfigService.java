package com.application.QueryGrid.Service.Configuration;

import com.application.QueryGrid.Entity.Configs.ConfigTypes;
import com.application.QueryGrid.Entity.Configs.DatabaseConfigs;
import com.application.QueryGrid.Entity.Group.Groups;
import com.application.QueryGrid.Entity.UserAuth.User;
import com.application.QueryGrid.Repository.ConfigsRepository;
import com.application.QueryGrid.Repository.GroupRepository;
import com.application.QueryGrid.Utils.GeneralUtils;
import com.application.QueryGrid.Utils.JDBCUtils;
import com.application.QueryGrid.Utils.PatchUtils;
import com.application.QueryGrid.Utils.XmlParser;
import com.application.QueryGrid.dto.request.Configs.ConfigCreateRequest;
import com.application.QueryGrid.dto.request.Configs.ConfigPatchRequest;
import com.application.QueryGrid.dto.request.Configs.TestDbRequest;
import com.application.QueryGrid.dto.response.Configs.ReturnConfig;
import com.application.QueryGrid.dto.response.Configs.ReturnConfigNames;
import com.application.QueryGrid.dto.response.Configs.ReturnConfigs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ConfigsRepository configsRepository;
    private final GroupRepository groupRepository;
    private final XmlParser xmlParser;

    public String testConnection(TestDbRequest dbRequest){
        String jdbcUrl = dbRequest.getJdbcUrl();
        String connectionMsg = "";
        try{
            connectionMsg = JDBCUtils.testJdbcConnection(jdbcUrl, dbRequest.getUsername(), dbRequest.getPassword(), 5);
        }catch (SQLException e){
            throw new RuntimeException("Failed to connect to database using provided configuration: " + e.getMessage(), e);
        }

        return connectionMsg;
    }


    public Set<Groups> getSelectedGroups(Set<String> group_names){
        Set<Groups> mappedgroups = Collections.emptySet();
        mappedgroups = groupRepository.findAllByGroupNameIn(group_names);

        Set<String> foundNames = mappedgroups.stream().map(Groups::getGroupName).collect(Collectors.toSet());
        Set<String> missing = new HashSet<>(group_names);
        missing.removeAll(foundNames);
        if(!missing.isEmpty()){
            throw new RuntimeException("Groups not found: "+ missing);
        }

        return mappedgroups;
    }

    @Transactional
    public String createConfig(ConfigCreateRequest createRequest) throws Exception {
        if(configsRepository.existsByConfigName(createRequest.getConfigName())){
            throw new RuntimeException("Config with name '" + createRequest.getConfigName() + "' already exists.");
        }

        TestDbRequest testDbRequest = TestDbRequest.builder()
                .jdbcUrl(JDBCUtils.buildJdbcUrl(createRequest.getDbType(), createRequest.getHost(), createRequest.getPort(), createRequest.getDatabaseName()))
                .username(createRequest.getUsername())
                .password(createRequest.getPassword())
                .build();

        String connectionMsg = testConnection(testDbRequest);

        Set<Groups> mappedgroups = Collections.emptySet();
        if(createRequest.getGroupNames() != null && !createRequest.getGroupNames().isEmpty()){
            mappedgroups = getSelectedGroups(createRequest.getGroupNames());
        }

        DatabaseConfigs dbConfigs = DatabaseConfigs.builder()
                .configName(createRequest.getConfigName())
                .configTypes(ConfigTypes.valueOf(createRequest.getConfigType()))
                .dbType(createRequest.getDbType())
                .host(createRequest.getHost())
                .port(createRequest.getConfigType().equals("CLOUD") ? 0 : createRequest.getPort())
                .databaseName(createRequest.getDatabaseName())
                .username(createRequest.getUsername())
                .password(GeneralUtils.encrypt(createRequest.getPassword()))
                .groups(mappedgroups)
                .build();
        configsRepository.save(dbConfigs);
        return "Database Configuration Saved Successful. \nConnection Message : " + connectionMsg;
    }

    public ReturnConfig getConfig(String configName) throws Exception{
        DatabaseConfigs dbConfigs = configsRepository.findByConfigName(configName).orElseThrow();

        return ReturnConfig.builder()
                .configName(configName)
                .configType(dbConfigs.getConfigTypes().name())
                .host(dbConfigs.getHost())
                .port(dbConfigs.getPort())
                .databaseName(dbConfigs.getDatabaseName())
                .username(dbConfigs.getUsername())
                .password(GeneralUtils.decrypt(dbConfigs.getPassword()))
                .dbType(dbConfigs.getDbType())
                .groupNames(dbConfigs.getGroups().stream().map(Groups::getGroupName).collect(Collectors.toSet()))
                .connectionUrl(JDBCUtils.buildJdbcUrl(dbConfigs.getDbType(), dbConfigs.getHost(), dbConfigs.getPort(), dbConfigs.getDatabaseName()))
                .build();
    }


    public Set<ReturnConfigNames> getConfigNames(){
        Set<ReturnConfigNames> configWithGroupNames = new HashSet<>();

        List<DatabaseConfigs> allConfigs = configsRepository.findAll();

        for(DatabaseConfigs config: allConfigs) {
            List<String> groupNames = config.getGroups()
                    .stream()
                    .map(Groups::getGroupName)
                    .toList();

            ReturnConfigNames configDto = ReturnConfigNames.builder()
                    .configName(config.getConfigName())
                    .groupNames(groupNames)
                    .build();

            configWithGroupNames.add(configDto);
        }

        return configWithGroupNames;
    }


    @Transactional
    public String patchConfig(ConfigPatchRequest configPatchRequest) throws Exception {
        DatabaseConfigs savedConfig = configsRepository.findByConfigName(configPatchRequest.getConfigName())
                .orElseThrow();

        if (configPatchRequest.getPassword() != null) {
            savedConfig.setPassword(GeneralUtils.encrypt(configPatchRequest.getPassword()));
            configPatchRequest.setPassword(null);
        }

        PatchUtils.copyNonNullProperties(configPatchRequest, savedConfig);

        if (configPatchRequest.getGroupNames() != null && !configPatchRequest.getGroupNames().isEmpty()) {
            Set<Groups> newGroups = new HashSet<>(groupRepository.findAllById(configPatchRequest.getGroupNames()));
            savedConfig.setGroups(newGroups);
        }

        configsRepository.save(savedConfig);

        return "Database Configuration Updated Successfully";
    }


    @Transactional
    public ReturnConfigs getConfigsByGroupName(Principal connectedUser) throws  Exception{
        var userEmail = ((User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal()).getEmail();
        String groupName = (groupRepository.findByuserEmail(userEmail).orElseThrow()).getGroupName();
        Set<DatabaseConfigs> savedConfigs = configsRepository.findAllByGroups_GroupName(groupName);

        Set<ReturnConfig> currentUserConfigs = savedConfigs.stream().map(
                db -> {
                    try {
                        return ReturnConfig.builder()
                                .configName(db.getConfigName())
                                .dbType(db.getDbType())
                                .configType(db.getConfigTypes().name())
                                .host(db.getHost())
                                .port(db.getPort())
                                .databaseName(db.getDatabaseName())
                                .username(db.getUsername())
                                .password(GeneralUtils.decrypt(db.getPassword()))
                                .groupNames(db.getGroups().stream().map(Groups::getGroupName).collect(Collectors.toSet()))
                                .connectionUrl(JDBCUtils.buildJdbcUrl(db.getDbType(), db.getHost(), db.getPort(), db.getDatabaseName()))
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).collect(Collectors.toSet());

        return ReturnConfigs.builder()
                .databaseConfigs(currentUserConfigs)
                .build();
    }


    public ReturnConfigs importConfiguration(MultipartFile file) throws  Exception{
        try(InputStream is = file.getInputStream()){
            return xmlParser.parseXml(is, ReturnConfigs.class);
        }catch (Exception e){
            throw  new Exception("Failed to process file: " + e.getMessage());
        }

    }

    public String saveConfiguration(ReturnConfigs importedConfigs) throws Exception{
        for(ReturnConfig imported: importedConfigs.getDatabaseConfigs()){
            ConfigCreateRequest createRequest = ConfigCreateRequest.builder()
                    .configName(imported.getConfigName())
                    .dbType(imported.getDbType())
                    .host(imported.getHost())
                    .port(null)
                    .databaseName(imported.getDatabaseName())
                    .username(imported.getUsername())
                    .password(imported.getPassword())
                    .configType(imported.getConfigType())
                    .groupNames(imported.getGroupNames())
                    .build();
            createConfig(createRequest);
        }
        return "Database Configuration Imported Successfully";

    }


    public <T> byte[] exportConfiguration(ReturnConfigs returnConfig) throws Exception{
        String configData = xmlParser.writeXml(returnConfig);
        return configData.getBytes(StandardCharsets.UTF_8);
    }




}
