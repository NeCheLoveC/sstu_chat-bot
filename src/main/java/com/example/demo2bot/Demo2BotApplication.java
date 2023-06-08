package com.example.demo2bot;

import com.example.demo2bot.services.NodeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Demo2BotApplication {

    public static void main(String[] args) {
        SpringApplication.run(Demo2BotApplication.class, args);
    }

    @Bean
    CommandLineRunner init(NodeService nodeService)
    {
        return (args) -> {
            if(nodeService.getRootNode().isEmpty())
                nodeService.createRootNode();
        };
    }

}
