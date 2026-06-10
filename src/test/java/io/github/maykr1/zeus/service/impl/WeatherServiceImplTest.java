package io.github.maykr1.zeus.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import io.github.maykr1.zeus.model.weather.LocationResponse;

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

    @Mock
    private UriBuilder uriBuilder;

    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherServiceImpl(restClient);
    }

    @Test
    void getLocation_shouldThrowException_whenResponseIsNull() {
        // Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(LocationResponse.class)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> weatherService.getTodayWeather());
    }

    @Test
    void getLocation_shouldThrowException_whenLatOrLonIsNull() {
        // Arrange
        LocationResponse locationResponse = new LocationResponse("NY", "New York", null, 12.34);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(LocationResponse.class)).thenReturn(locationResponse);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> weatherService.getTodayWeather());
    }
}
