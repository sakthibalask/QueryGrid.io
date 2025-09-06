package com.application.QueryGrid.io.dto.response.Configs;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "DatabaseConfigs")
public class ReturnConfigs {
    private Set<ReturnConfig> databaseConfigs;
}
