package com.example.demo2bot.services;

import com.example.demo2bot.config.TelegramBot;
import com.example.demo2bot.model.TelegramUser;
import com.example.demo2bot.repo.TelegramUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class TelegramUserService
{
    TelegramUserRepository telegramUserRepository;

    @Autowired
    public TelegramUserService(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    public void save(TelegramUser telegramUser)
    {
        telegramUserRepository.save(telegramUser);
    }

    public void remove(TelegramUser telegramUser)
    {
        telegramUserRepository.delete(telegramUser);
    }

    public TelegramUser findByChatId(Long chatId)
    {
        return telegramUserRepository.getTelegramUserById(chatId);
    }
}
