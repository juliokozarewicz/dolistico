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
    public LinkedHashMap<String, Object> getDeviceByUserAgent(

        Locale locale,
        String userAgent

    ) {

        try {

            if (userAgent == null || userAgent.isBlank()) {
                throw new RuntimeException();
            }

            String ua = userAgent.toLowerCase();

            // Browser
            String browser = null;
            String browserVersion = null;

            if (
                ua.contains("edg/") ||
                ua.contains("edga/") ||
                ua.contains("edgios/")
            ) {
                browser = "Edge";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "(?:Edg[A-Za-z]*/|Edge/)([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("opr/") || ua.contains("opera/")) {
                browser = "Opera";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "(?:OPR|Opera)/([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("samsungbrowser/")) {
                browser = "Samsung Browser";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "SamsungBrowser/([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("ucbrowser/")) {
                browser = "UC Browser";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "UCBrowser/([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("firefox/") || ua.contains("fxios/")) {
                browser = "Firefox";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "(?:Firefox|FxiOS)/([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("chromium/")) {
                browser = "Chromium";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "Chromium/([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("chrome/")) {
                browser = "Chrome";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "Chrome/([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("safari/") && !ua.contains("chrome")) {
                browser = "Safari";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "Version/([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("msie") || ua.contains("trident/")) {
                browser = "Internet Explorer";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "(?:MSIE |rv:)([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    browserVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            }

            // OS
            String os = null;
            String osVersion = null;

            if (ua.contains("android")) {
                os = "Android";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "Android ([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    osVersion = m.find() ? m.group(1) : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("iphone")) {
                os = "iOS";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "OS ([\\d_]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    osVersion = m.find()
                        ? m.group(1).replace("_", ".")
                        : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("ipad")) {
                os = "iPadOS";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "OS ([\\d_]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    osVersion = m.find()
                        ? m.group(1).replace("_", ".")
                        : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("windows nt")) {
                os = "Windows";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "Windows NT ([\\d.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    if (m.find()) {
                        osVersion = switch (m.group(1)) {
                            case "10.0" -> "10/11";
                            case "6.3"  -> "8.1";
                            case "6.2"  -> "8";
                            case "6.1"  -> "7";
                            case "6.0"  -> "Vista";
                            case "5.1"  -> "XP";
                            default     -> m.group(1);
                        };
                    }
                } catch (Exception ignored) {}

            } else if (ua.contains("mac os x") || ua.contains("macos")) {
                os = "macOS";

                try {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile(
                            "Mac OS X ([\\d_.]+)",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                        )
                        .matcher(userAgent);

                    osVersion = m.find()
                        ? m.group(1).replace("_", ".")
                        : null;
                } catch (Exception ignored) {}

            } else if (ua.contains("ubuntu")) {
                os = "Ubuntu (Linux)";
            } else if (ua.contains("debian")) {
                os = "Debian (Linux)";
            } else if (ua.contains("fedora")) {
                os = "Fedora (Linux)";
            } else if (ua.contains("arch")) {
                os = "Arch (Linux)";
            } else if (ua.contains("cros")) {
                os = "ChromeOS";
            } else if (ua.contains("linux")) {
                os = "Linux";
            }

            // Architecture
            String arch = null;

            if (
                ua.contains("x86_64") ||
                ua.contains("win64") ||
                ua.contains("wow64")
            ) {
                arch = "x86_64";
            } else if (
                ua.contains("arm64") ||
                ua.contains("aarch64")
            ) {
                arch = "ARM64";
            } else if (ua.contains("arm")) {
                arch = "ARM";
            } else if (
                ua.contains("i686") ||
                ua.contains("i386")
            ) {
                arch = "x86";
            }

            // Platform
            String platform = null;

            if (ua.contains("android") || ua.contains("iphone")) {
                platform = "Mobile";
            } else if (ua.contains("ipad")) {
                platform = "Tablet";
            } else if (
                ua.contains("windows") ||
                ua.contains("mac") ||
                ua.contains("linux") ||
                ua.contains("x11") ||
                ua.contains("cros")
            ) {
                platform = "Desktop";
            }

            if (
                browser == null &&
                os == null &&
                platform == null
            ) {
                throw new RuntimeException();
            }

            // Description
            StringBuilder description = new StringBuilder();

            if (browser != null) {
                description.append(browser);

                if (
                    browserVersion != null &&
                    !browserVersion.isBlank()
                ) {
                    description.append(" ").append(browserVersion);
                }
            }

            if (os != null) {
                if (!description.isEmpty()) {
                    description.append(", ");
                }

                description.append(os);

                if (osVersion != null && !osVersion.isBlank()) {
                    description.append(" ").append(osVersion);
                }
            }

            if (arch != null) {
                if (!description.isEmpty()) {
                    description.append(" (");
                }

                description.append(arch).append(")");
            }

            if (platform != null) {
                if (!description.isEmpty()) {
                    description.append(" - ");
                }

                description.append(platform);
            }

            LinkedHashMap<String, Object> device = new LinkedHashMap<>();

            device.put("status", "success");
            device.put("description", description.toString());
            device.put("browser", browser);
            device.put("browserVersion", browserVersion);
            device.put("os", os);
            device.put("osVersion", osVersion);
            device.put("arch", arch);
            device.put("platform", platform);

            return device;

        } catch (Exception e) {

            LinkedHashMap<String, Object> device = new LinkedHashMap<>();

            device.put("status", "error");
            device.put(
                "description",
                messageSource.getMessage(
                    "email_device_error",
                    null,
                    locale
                )
            );

            return device;
        }
    }

}
