package io.github.maykr1.zeus.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.maykr1.zeus.model.weather.WeatherResponse;
import io.github.maykr1.zeus.service.WeatherService;

@WebMvcTest(ZeusController.class)
class ZeusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Test
    void getTodayWeather_shouldReturnOk() throws Exception {
        WeatherResponse weather = new WeatherResponse("America/New_York", "EST", null, null);
        when(weatherService.getTodayWeather()).thenReturn(weather);

        mockMvc.perform(get("/api/weather/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timezone").value("America/New_York"));
    }

    @Test
    void getWeeklyWeather_shouldReturnOk() throws Exception {
        WeatherResponse weather = new WeatherResponse("America/New_York", "EST", null, null);
        when(weatherService.getWeeklyWeather()).thenReturn(weather);

        mockMvc.perform(get("/api/weather/weekly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timezone").value("America/New_York"));
    }

    @Test
    void handleException_shouldReturnInternalServerError() throws Exception {
        when(weatherService.getTodayWeather()).thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(get("/api/weather/today"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Test exception"));
    }
}
