package com.application.QueryGrid.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonXmlConfig {

    @Bean
    @Primary  // This will be the default for JSON
    public ObjectMapper jsonMapper() {
        return JsonMapper.builder().build();
    }

    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper xml = new XmlMapper();
        xml.findAndRegisterModules(); // optional, registers modules
        return xml;

    }
}
