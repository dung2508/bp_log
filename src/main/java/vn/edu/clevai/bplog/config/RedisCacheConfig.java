package vn.edu.clevai.bplog.config;

import java.time.Duration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.queue.RedisMessageSubscriber;
import vn.edu.clevai.common.api.util.RedisKeyPrefixInitiation;

@Configuration
@Slf4j
@EnableCaching
@Order(100)
public class RedisCacheConfig extends RedisKeyPrefixInitiation implements RegionCacheSupporter {

	@Value("${cache.redis.host}")
	private String redisHost;

	@Value("${cache.redis.port}")
	private Integer redisPort;

	public RedisCacheConfig(@Value("${spring.application.name}") String keyPrefix, Environment environment) {
		super(!StringUtils.isEmpty(keyPrefix) ? keyPrefix : "clevai-bp-log-service", environment);
		log.info("Key prefix is {}", keyPrefix);
	}

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort));
	}

	@Bean(name = "redisTemplate")
	RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisSerializer<?> genericJackson2JsonRedisSerializer = redisSerializer();
		RedisSerializer<?> stringRedisSerializer = new StringRedisSerializer();
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
		redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
		redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}

	@Bean
	RedisSerializer<?> redisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	@Bean
	@Primary
	public CacheManager initRedisCacheManager(RedisConnectionFactory factory) {
		log.info("Start enable redis cache");
		RedisSerializationContext.SerializationPair<Object> jsonSerializer = RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer());
		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
				.fromConnectionFactory(factory)
				.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(getCacheKeyPrefix())
						.entryTtl(Duration.ofSeconds(300)).serializeValuesWith(jsonSerializer))
				.withCacheConfiguration(BP_TASK_CACHE_REGION,
						RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(getCacheKeyPrefix())
								.entryTtl(Duration.ofSeconds(BP_TASK_CACHE_DURATION))
								.serializeValuesWith(jsonSerializer));

		return builder.build();
	}

	@Bean
	public RedissonClient getRedissonClient() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);
		return Redisson.create(config);
	}

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
											MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, topic());
		return container;
	}

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("messageQueue");
	}

	@Bean
	MessageListenerAdapter messageListener(@Autowired RedisMessageSubscriber sibscriber) {
		return new MessageListenerAdapter(sibscriber);
	}

	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

}
