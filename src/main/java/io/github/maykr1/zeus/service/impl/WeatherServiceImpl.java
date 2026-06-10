package io.github.maykr1.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import io.github.maykr1.zeus.model.weather.LocationResponse;
import io.github.maykr1.zeus.model.weather.WeatherResponse;
import io.github.maykr1.zeus.service.WeatherService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private final RestClient restClient;
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
        long start                  = System.currentTimeMillis();
        LocationResponse location   = getLocation();

        logger.info("Retrieving {} weather in {}, {}...", type, location.city(), location.state());

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

    private LocationResponse getLocation() {
        long start                  = System.currentTimeMillis();
        LocationResponse response   = null;

        logger.info("Retrieving location...");

        try {
            response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host("ip-api.com")
                    .path("json")
                    .queryParam("fields", "region,city,lat,lon")
                    .build()
                )
                .retrieve()
                .body(LocationResponse.class);

            if (response == null || response.latitude() == null || response.longitude() == null) {
                logger.error("[RuntimeException] - Error occurred while retrieving location");
                throw new RuntimeException("Failed to retrieve location: Latitude or Longitude is null");
            }
            
        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected error occurred while retrieving location", e);
            throw e;
        }

        logger.info("[{} ms] - Finished retrieving location => lat: {}, lon: {}", System.currentTimeMillis() - start, response.latitude(), response.longitude());
        return response;
    }
}
