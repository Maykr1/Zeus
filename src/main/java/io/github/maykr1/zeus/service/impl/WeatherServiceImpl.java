package io.github.maykr1.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import io.github.maykr1.zeus.model.weather.Location;
import io.github.maykr1.zeus.model.weather.WeatherResponse;
import io.github.maykr1.zeus.service.WeatherService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private final RestClient restClient;
    private final Location location;
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);

    @Override
    public WeatherResponse getTodayWeather() {
        return getWeather("today", 1, "hourly", "temperature_2m");
    }

    @Override
    public WeatherResponse getWeeklyWeather() {
        return getWeather("weekly", 7, "daily", "temperature_2m_max,temperature_2m_min");
    }

    private WeatherResponse getWeather(String type, int days, String paramName, String paramValue) {
        long start = System.currentTimeMillis();

        logger.info("Retrieving {} weather in {}, {}...", type, location.latitude(), location.longitude());

        try {
            WeatherResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("api.open-meteo.com")
                    .pathSegment("v1", "forecast")
                    .queryParam("latitude", location.latitude())
                    .queryParam("longitude", location.longitude())
                    .queryParam("models", "gfs_seamless")
                    .queryParam("timezone", "America/New_York")
                    .queryParam("forecast_days", String.valueOf(days))
                    .queryParam("temperature_unit", "fahrenheit")
                    .queryParam(paramName, paramValue)
                    .build()
                )
                .retrieve()
                .body(WeatherResponse.class);

            logger.info("[{} ms] - Finished retrieving {} weather", System.currentTimeMillis() - start, type);
            return response;

        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected error occurred while retrieving {} weather", type, e);
            throw e;
        }
    }
}
