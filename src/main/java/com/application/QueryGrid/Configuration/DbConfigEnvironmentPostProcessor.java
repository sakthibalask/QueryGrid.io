package com.application.QueryGrid.Configuration;

import com.application.QueryGrid.dto.AppDatabaseConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DbConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    private static final String PROP_SOURCE_NAME = "externalJsonDbConfig";
    private static final String DB_CONFIG_PATH_PROP = "db.config.path";
    private static final String DEFAULT_JSON_PATH_WINDOWS = "C:\\Application\\product\\data\\dbconfig.json";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application){
        String jsonPath = environment.getProperty(DB_CONFIG_PATH_PROP);
        if(jsonPath == null || jsonPath.isBlank()) {
            jsonPath = DEFAULT_JSON_PATH_WINDOWS;
        }

        Path p = Paths.get(jsonPath);
        if(!Files.exists(p)) {
            System.out.println("[DbConfig] No JSON DB config found at: " + jsonPath + " — using bundled application.properties");
            return;
        }

        try{
            byte[] bytes = Files.readAllBytes(p);
            AppDatabaseConfig cfg = mapper.readValue(bytes, AppDatabaseConfig.class);

            Map<String, Object> map = new HashMap<>();

            if(cfg.getUrl() != null && !cfg.getUrl().isBlank()) {
                map.put("spring.datasource.url", cfg.getUrl());
            }

            if (cfg.getUsername() != null && !cfg.getUsername().isBlank()) {
                map.put("spring.datasource.username", cfg.getUsername());
            }

            if (cfg.getPassword() != null && !cfg.getPassword().isBlank()) {
                map.put("spring.datasource.password", cfg.getPassword());
            }

            if (cfg.getDriverClassName() != null && !cfg.getDriverClassName().isBlank()) {
                map.put("spring.datasource.driver-class-name", cfg.getDriverClassName());
            }

            if (cfg.getHibernateDdlAuto() != null && !cfg.getHibernateDdlAuto().isBlank()) {
                map.put("spring.jpa.hibernate.ddl-auto", cfg.getHibernateDdlAuto());
            }
            if(cfg.getHibernateDialect() != null && !cfg.getHibernateDialect().isBlank()) {
                map.put("spring.jpa.properties.hibernate.dialect", cfg.getHibernateDialect());
            }
            if (map.isEmpty()) {
                System.out.println("[DbConfig] JSON config at " + jsonPath + " contained no DB properties to apply.");
                return;
            }

            for(String key: map.keySet()){
                String existing = environment.getProperty(key);
                if(existing == null) {
                    System.out.println("[DbConfig] Will add property " + key + "=" + maskIfPassword(key, map.get(key)));
                }else if(!existing.equals(String.valueOf(map.get(key)))){
                    System.out.println("[DbConfig] Will override property " + key + " (existing='" + maskIfPassword(key, existing) + "' => new='" + maskIfPassword(key, map.get(key)) + "')");
                }else{
                    System.out.println("[DbConfig] Property " + key + " from JSON equals existing environment value; still applying with higher precedence.");
                }
            }

            MutablePropertySources sources = environment.getPropertySources();
            MapPropertySource mps = new MapPropertySource(PROP_SOURCE_NAME, map);
            sources.addFirst(mps);

            System.out.println("[DbConfig] Applied JSON DB config from: " + jsonPath);
        }catch (IOException e) {
            System.err.println("[DbConfig] Failed to read/parse JSON DB config at " + jsonPath + ": " + e.getMessage());
//            e.printStackTrace();
        }catch (Exception ex) {
            System.err.println("[DbConfig] Unexpected error while applying JSON DB config: " + ex.getMessage());
//            ex.printStackTrace();
        }
    }

    private static String maskIfPassword(String key, Object value) {
        if (key != null && key.toLowerCase().contains("password")) {
            return "*****";
        }
        return String.valueOf(value);
    }

    @Override
    public int getOrder(){
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }
}
