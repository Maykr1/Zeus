package io.github.maykr1.zeus.model.weather;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "zeus.location")
public record LocationProperties(
    @NotNull @Min(-90) @Max(90) Double latitude,
    @NotNull @Min(-90) @Max(90) Double longitude
) {}
