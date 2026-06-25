package io.github.maykr1.zeus.model.weather;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zeus.location")
public record Location(
    Double latitude,
    Double longitude
) {}
