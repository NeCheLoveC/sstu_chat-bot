package com.example.demo2bot.config;

import org.glassfish.grizzly.http.util.UDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class MessageRedisControl implements MessageListener
{
    private TelBot telBot;

    @Autowired
    public MessageRedisControl() {
    }
    @Autowired
    public void setTelBot(@Qualifier("telBot") TelBot telBot) {
        this.telBot = telBot;
        System.out.println("Установлен!!!");
    }

    @Override
    public void onMessage(Message message, byte[] pattern)
    {
        String text = message.toString();
        telBot.sendMessageToAllSubscribes(text);
    }
}
