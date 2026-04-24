package juliokozarewicz.accounts.infrastructure;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // JSON serializer for cache values — human-readable and type-safe
        RedisSerializer<Object> valueSerializer = RedisSerializer.json();

        // String serializer for cache keys — keeps keys clean and predictable
        RedisSerializer<String> keySerializer = RedisSerializer.string();

        // Wrap serializers into serialization pairs required by RedisCacheConfiguration
        RedisSerializationContext.SerializationPair<Object> valuePair =
                RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer);

        RedisSerializationContext.SerializationPair<String> keyPair =
                RedisSerializationContext.SerializationPair.fromSerializer(keySerializer);

        // Default cache configuration applied to all caches unless overridden
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .disableCachingNullValues()
            .serializeValuesWith(valuePair)
            .serializeKeysWith(keyPair);

        // Per-cache TTL overrides — each entry extends or replaces the default TTL
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        // Long-lived user data: profile and address rarely change
        cacheConfigs.put("accounts.profileCache", defaultCacheConfig.entryTtl(Duration.ofDays(180)));

        cacheConfigs.put("accounts.addressCache", defaultCacheConfig.entryTtl(Duration.ofDays(180)));

        // Short-lived PIN verification — expires in 5 minutes for security
        cacheConfigs.put("accounts.pinVerificationCache", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));

        // Holds state for accounts pending activation — kept for 3 days
        cacheConfigs.put("accounts.notActivatedAccountCache", defaultCacheConfig.entryTtl(Duration.ofDays(3)));

        // Grace period after user-initiated account deletion — 32 days for recovery
        cacheConfigs.put("accounts.deletedAccountByUserCache", defaultCacheConfig.entryTtl(Duration.ofDays(32)));

        // Build the RedisCacheManager with the default config and all per-cache overrides
        return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(defaultCacheConfig)
        .withInitialCacheConfigurations(cacheConfigs)
        .build();

    }

}