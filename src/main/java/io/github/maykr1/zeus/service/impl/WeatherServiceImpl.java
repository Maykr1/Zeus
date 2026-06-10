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

    public LocationResponse getLocation() {
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

            if (response.latitude() == null || response.longitude() == null) {
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

    @Override
    public WeatherResponse getTodayWeather() {
        long start                  = System.currentTimeMillis();
        WeatherResponse response    = null;
        LocationResponse location   = getLocation();

        logger.info("Retrieving today's weather in {}, {}...", location.city(), location.state());

        try {
            response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("api.open-meteo.com")
                    .pathSegment("v1", "forecast")
                    .queryParam("latitude", location.latitude())
                    .queryParam("longitude", location.longitude())
                    .queryParam("hourly", "temperature_2m")
                    .queryParam("models", "gfs_seamless")
                    .queryParam("timezone", "America/New_York")
                    .queryParam("forecast_days", "1")
                    .queryParam("temperature_unit", "fahrenheit")
                    .build()
                )
                .retrieve()
                .body(WeatherResponse.class);

        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected error occurred while retrieving weather", e);
            throw e;
        }
        
        logger.info("[{} ms] - Finished retrieving today's weather", System.currentTimeMillis() - start);
        return response;
    }

    @Override
    public WeatherResponse getWeeklyWeather() {
        long start                  = System.currentTimeMillis();
        WeatherResponse response    = null;
        LocationResponse location   = getLocation();

        logger.info("Retrieving weekly weather in {}, {}...", location.city(), location.state());

        try {
            response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("api.open-meteo.com")
                    .pathSegment("v1", "forecast")
                    .queryParam("latitude", location.latitude())
                    .queryParam("longitude", location.longitude())
                    .queryParam("models", "gfs_seamless")
                    .queryParam("timezone", "America/New_York")
                    .queryParam("forecast_days", "7")
                    .queryParam("temperature_unit", "fahrenheit")
                    .queryParam("daily", "temperature_2m_max,temperature_2m_min")
                    .build()
                )
                .retrieve()
                .body(WeatherResponse.class);

        } catch (Exception e) {
            logger.error("[UnexpectedException] - Unexpected error occurred while retrieving weekly weather", e);
            throw e;
        }

        logger.info("[{} ms] - Finished retrieving weekly weather", System.currentTimeMillis() - start);
        return response;
    }
}
