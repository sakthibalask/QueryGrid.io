package com.application.QueryGrid.Utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class XmlParser {
    private final XmlMapper xmlMapper;

    public <T> T parseXml(InputStream is, Class<T> clazz) throws Exception {
        try{
            return xmlMapper.readValue(is, clazz);
        }catch (Exception e){
            throw new Exception("Failed to parse XML to " + clazz.getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    public <T> String writeXml(T dto) throws Exception {
        try{
            return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        }catch (Exception e){
            throw new Exception("Failed to convert DTO to XML: " + e.getMessage(), e);
        }
    }
}
