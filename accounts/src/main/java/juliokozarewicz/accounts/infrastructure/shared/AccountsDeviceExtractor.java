package juliokozarewicz.accounts.infrastructure.shared;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Component
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

            if (geoData == null || !"success".equals(String.valueOf(geoData.get("status")))) {
                throw new RuntimeException();
            }

            String city = String.valueOf( geoData.getOrDefault("city", "") );
            String state = String.valueOf(geoData.getOrDefault("regionName", ""));
            String countryCode = String.valueOf(geoData.getOrDefault("countryCode", ""));
            String country = countryCode.isBlank() ? "" : new Locale("", countryCode).getDisplayCountry(locale);
            Double lat = geoData.get("lat") != null ? ((Number) geoData.get("lat")).doubleValue() : null;
            Double lon = geoData.get("lon") != null ? ((Number) geoData.get("lon")).doubleValue() : null;
            if (city.isBlank() && state.isBlank() && country.isBlank()) { throw new RuntimeException(); }

            LinkedHashMap<String, Object> location = new LinkedHashMap<>();
            location.put("status", "success");
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
            location.put("status", "error");
            location.put(
                "description",
                messageSource.getMessage(
                    "email_location_error", null, locale
                )
            );
            return location;

        }

    }

    // User device agent
    public LinkedHashMap<String, Object> getDeviceByUserAgent (

        Locale locale,
        String userAgent


    ) {

        try {

            if (userAgent == null || userAgent.isBlank()) {
            throw new RuntimeException();
        }

        String ua = userAgent.toLowerCase();

        String browser = null;
        if (ua.contains("edg")) {
            browser = "Edge";
        } else if (ua.contains("chrome") && !ua.contains("edg")) {
            browser = "Chrome";
        } else if (ua.contains("firefox")) {
            browser = "Firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            browser = "Safari";
        }

        String os = null;
        if (ua.contains("android")) {
            os = "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            os = "iOS";
        } else if (ua.contains("windows")) {
            os = "Windows";
        } else if (ua.contains("mac")) {
            os = "macOS";
        }

        String platform = null;
        if (ua.contains("android") || ua.contains("iphone") || ua.contains("ipad")) {
            platform = "Mobile";
        } else if (ua.contains("windows") || ua.contains("mac") || ua.contains("linux")) {
            platform = "Desktop";
        }

        // se tudo falhou → fallback
        if (browser == null && os == null && platform == null) {
            throw new RuntimeException();
        }

        // monta descrição só com o que existe
        String description = "";

        if (browser != null) {
            description += browser;
        }

        if (os != null) {
            if (!description.isEmpty()) description += ", ";
            description += os;
        }

        if (platform != null) {
            if (!description.isEmpty()) description += " - ";
            description += platform;
        }

        LinkedHashMap<String, Object> device = new LinkedHashMap<>();
        device.put("status", "success");
        device.put("description", description);
        device.put("browser", browser);
        device.put("os", os);
        device.put("platform", platform);

        return device;

        } catch (Exception e) {

            // Fallback in case of unexpected errors
            LinkedHashMap<String, Object> device = new LinkedHashMap<>();
            device.put("status", "error");
            device.put(
                "description",
                messageSource.getMessage(
                    "email_device_error", null, locale
                )
            );
            return device;
        }
    }

}
