package io.github.maykr1.zeus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.maykr1.zeus.model.weather.WeatherResponse;
import io.github.maykr1.zeus.service.WeatherService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ZeusController {
    private final WeatherService weatherService;
    
    @GetMapping("/weather/today")
    public ResponseEntity<WeatherResponse> getTodayWeather() {        
        WeatherResponse response = weatherService.getTodayWeather();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/weather/weekly")
    public ResponseEntity<WeatherResponse> getWeeklyWeather() {
        WeatherResponse response = weatherService.getWeeklyWeather();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
