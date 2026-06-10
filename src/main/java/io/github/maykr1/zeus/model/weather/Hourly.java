package io.github.maykr1.zeus.model.weather;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Hourly(
    @JsonProperty("time")
    List<String> timestamps,

    @JsonProperty("temperature_2m")
    List<Double> temperatures
) {}
