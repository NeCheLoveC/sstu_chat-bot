package com.example.demo2bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource("application.properties")
public class BotConfig
{
    @Value("${bot.name}")
    String botName;
    @Value("${bot.key}")
    String token;
}
