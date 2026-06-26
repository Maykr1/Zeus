package io.github.maykr1.zeus.model.weather;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "zeus.location")
public record LocationProperties(
    @NotNull
    Double latitude,

    @NotNull
    Double longitude
) {}
