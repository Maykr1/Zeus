package io.github.maykr1.zeus.model.weather;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ModelTest {

    @Test
    void testLocationResponse() {
        LocationResponse location = new LocationResponse("NY", "New York", 40.71, -74.01);
        assertEquals("NY", location.state());
        assertEquals("New York", location.city());
        assertEquals(40.71, location.latitude());
        assertEquals(-74.01, location.longitude());
    }

    @Test
    void testWeatherResponse() {
        Hourly hourly = new Hourly(null, null);
        Daily daily = new Daily(null, null, null);
        WeatherResponse weather = new WeatherResponse("America/New_York", "EST", hourly, daily);
        
        assertEquals("America/New_York", weather.timezone());
        assertEquals("EST", weather.timezone_abbreviation());
        assertEquals(hourly, weather.hourly());
        assertEquals(daily, weather.daily());
    }

    @Test
    void testHourly() {
        Hourly hourly = new Hourly(null, null);
        assertNull(hourly.timestamps());
        assertNull(hourly.temperatures());
    }

    @Test
    void testDaily() {
        Daily daily = new Daily(null, null, null);
        assertNull(daily.timestamps());
        assertNull(daily.temperatureMax());
        assertNull(daily.temperatureMin());
    }
}
