package juliokozarewicz.accounts.infrastructure.shared;

import org.springframework.context.MessageSource;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class AccountsDeviceExtractor {

    // ==================================================== ( constructor init )

    // Env
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    private final MessageSource messageSource;
    private final WebClient webClient;

    public AccountsDeviceExtractor (

        MessageSource messageSource,
        WebClient webClient

    ) {

        this.messageSource = messageSource;
        this.webClient = webClient;

    }

    // ===================================================== ( constructor end )

    // Location by IP
    public LinkedHashMap<String, Object> getLocationByIp (

        String ip,
        Locale locale

    ) {

        try {

            Map<String, Object> geoData = webClient
            .get()
            .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("ip-api.com")
                .path("/json/{ip}")
                .queryParam(
                    "fields",
                    "status,countryCode,regionName,city,lat,lon"
                )
                .build(ip)
            )
            .retrieve()
            .bodyToMono(Map.class)
            .block();

            String city = String.valueOf( geoData.getOrDefault("city", "") );
            String state = String.valueOf(geoData.getOrDefault("regionName", ""));
            String countryCode = String.valueOf(geoData.getOrDefault("countryCode", ""));
            String country = new Locale("", countryCode).getDisplayCountry(locale);
            Double lat = geoData.get("lat") != null ? ((Number) geoData.get("lat")).doubleValue() : null;
            Double lon = geoData.get("lon") != null ? ((Number) geoData.get("lon")).doubleValue() : null;

            LinkedHashMap<String, Object> location = new LinkedHashMap<>();
            location.put("description", city + ", " + state + " - " + country);
            location.put("city", city);
            location.put("state", state);
            location.put("country", country);
            location.put("countryCode", countryCode);
            location.put("lat", lat);
            location.put("lon", lon);
            return location;

        } catch (Exception e) {

            LinkedHashMap<String, Object> location = new LinkedHashMap<>();
            location.put(
                "description",
                messageSource.getMessage(
                    "email_location_error", null, locale
                )
            );
            location.put("city", null);
            location.put("state", null);
            location.put("country", null);
            location.put("countryCode", null);
            location.put("lat", null);
            location.put("lon", null);
            return location;

        }

    }

    // User device agent
    public LinkedHashMap<String, Object> getDeviceByUserAgent (

        Locale locale,
        String userAgent


    ) {

        try {

            // Result map
            LinkedHashMap<String, Object> device = new LinkedHashMap<>();

            // Handle null or empty User-Agent (fallback inline)
            if (userAgent == null || userAgent.isBlank()) {

                device.put(
                    "description",
                    messageSource.getMessage(
                        "email_device_error", null, locale
                    )
                );
                device.put("browser", null);
                device.put("os", null);
                device.put("platform", null);
                return device;
            }

            // Normalize User-Agent for easier detection
            String ua = userAgent.toLowerCase();

            // Detect browser type
            String browser;
            if (ua.contains("edg")) {
                browser = "Edge";
            } else if (ua.contains("chrome") && !ua.contains("edg")) {
                browser = "Chrome";
            } else if (ua.contains("firefox")) {
                browser = "Firefox";
            } else if (ua.contains("safari") && !ua.contains("chrome")) {
                browser = "Safari";
            } else {
                browser = "Unknown";
            }

            // Detect operating system
            String os;
            if (ua.contains("android")) {
                os = "Android";
            } else if (ua.contains("iphone") || ua.contains("ipad")) {
                os = "iOS";
            } else if (ua.contains("windows")) {
                os = "Windows";
            } else if (ua.contains("mac")) {
                os = "macOS";
            } else {
                os = "Unknown";
            }

            // Detect device platform type
            String platform;
            if (ua.contains("android") || ua.contains("iphone") || ua.contains("ipad")) {
                platform = "Mobile";
            } else {
                platform = "Desktop";
            }

            // Human-readable description
            String description = browser + ", " + os + " - " + platform;

            // Fill response map
            device.put("description", description);
            device.put("browser", browser);
            device.put("os", os);
            device.put("platform", platform);
            return device;

        } catch (Exception e) {

            // Fallback in case of unexpected errors
            LinkedHashMap<String, Object> device = new LinkedHashMap<>();
            device.put(
                "description",
                messageSource.getMessage(
                    "email_device_error", null, locale
                )
            );
            device.put("browser", null);
            device.put("os", null);
            device.put("platform", null);
            return device;
        }
    }

}
