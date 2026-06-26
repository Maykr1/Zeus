package io.github.maykr1.zeus.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import io.github.maykr1.zeus.model.weather.LocationProperties;
import io.github.maykr1.zeus.model.weather.WeatherResponse;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private LocationProperties location;

    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {
        location = new LocationProperties(40.71, -74.01);
        weatherService = new WeatherServiceImpl(restClient, location);
    }

    @Test
    void getTodayWeather_shouldReturnResponse_whenSuccessful() {
        WeatherResponse weather = new WeatherResponse("America/New_York", "EST", null, null);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        ArgumentCaptor<Function<UriBuilder, java.net.URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
        when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(WeatherResponse.class)).thenReturn(weather);

        WeatherResponse result = weatherService.getTodayWeather();

        assertNotNull(result);
        assertEquals("America/New_York", result.timezone());
        
        for (Function<UriBuilder, java.net.URI> func : uriFunctionCaptor.getAllValues()) {
            UriBuilder mockBuilder = mock(UriBuilder.class, RETURNS_SELF);
            func.apply(mockBuilder);
        }
    }

    @Test
    void getWeeklyWeather_shouldReturnResponse_whenSuccessful() {
        WeatherResponse weather = new WeatherResponse("America/New_York", "EST", null, null);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        ArgumentCaptor<Function<UriBuilder, java.net.URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
        when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(WeatherResponse.class)).thenReturn(weather);

        WeatherResponse result = weatherService.getWeeklyWeather();

        assertNotNull(result);
        
        for (Function<UriBuilder, java.net.URI> func : uriFunctionCaptor.getAllValues()) {
            UriBuilder mockBuilder = mock(UriBuilder.class, RETURNS_SELF);
            func.apply(mockBuilder);
        }
    }

    @Test
    void getWeather_shouldThrowException_whenRestClientFails() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(WeatherResponse.class)).thenThrow(new RuntimeException("API error"));

        assertThrows(RuntimeException.class, () -> weatherService.getTodayWeather());
    }
}
