package io.github.maykr1.zeus.model.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocationResponse(
    @JsonProperty("region")
    String state,

    String city,

    @JsonProperty("lat")
    Double latitude,

    @JsonProperty("lon")
    Double longitude
) {}
