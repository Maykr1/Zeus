package io.github.maykr1.zeus.model.weather;

public record WeatherResponse(
    String timezone,
    String timezone_abbreviation,
    Hourly hourly,
    Daily daily
) {}
