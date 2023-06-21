package com.example.demo2bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig
{
    @Value("${cust.redis.port}")
    protected int port;
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", port);
        config.setPassword("redispw");
        return new JedisConnectionFactory(config);
    }

    @Bean
    public <K,V> RedisTemplate<K, V> redisTemplate() {
        final RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory());
        template.setDefaultSerializer(new StringRedisSerializer());
        //template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        template.afterPropertiesSet();
        return template;
    }


    @Bean
    protected ChannelTopic messageTopic() {
        return new ChannelTopic("message:channel");
    }

    @Bean
    protected MessageListenerAdapter messageListenerAdapter(MessageRedisControl messageRedisControl)
    {
        return new MessageListenerAdapter(messageRedisControl);
    }

    @Bean
    protected RedisMessageListenerContainer messageListenerContainer(MessageListenerAdapter messageListenerAdapter)
    {
        RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(redisConnectionFactory());
        messageListenerContainer.addMessageListener(messageListenerAdapter,messageTopic());
        return messageListenerContainer;
    }
}
