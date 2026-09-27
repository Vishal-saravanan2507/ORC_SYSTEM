package com.orc.wasteheat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Industrial Waste Heat to ORC Electricity Generation
 * and Monitoring System.
 *
 * Once started, the application (backend + static frontend) is available at:
 * http://localhost:8080
 */
@SpringBootApplication
public class WasteHeatOrcApplication {
    public static void main(String[] args) {
        SpringApplication.run(WasteHeatOrcApplication.class, args);
    }
}
