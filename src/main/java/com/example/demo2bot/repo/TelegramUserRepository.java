package com.example.demo2bot.repo;

import com.example.demo2bot.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long>
{
    @Query("select tu from TelegramUser tu where tu.id = :id")
    public TelegramUser getTelegramUserById(Long id);
}