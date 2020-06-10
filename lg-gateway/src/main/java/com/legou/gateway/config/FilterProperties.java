package com.legou.gateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Data
@Component
@Slf4j
@ConfigurationProperties("lg.filter")
public class FilterProperties {
    private Map<String, Set<String>> allowRequests;
}
