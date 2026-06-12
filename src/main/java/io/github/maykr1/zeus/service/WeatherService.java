package io.github.maykr1.zeus.service;

import io.github.maykr1.zeus.model.weather.WeatherResponse;

public interface WeatherService {
    public WeatherResponse getTodayWeather();
    public WeatherResponse getWeeklyWeather();
}
