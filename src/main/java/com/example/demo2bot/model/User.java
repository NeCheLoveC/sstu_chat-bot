package com.example.demo2bot.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "userrrr")
public class User
{
    @Id
    protected Long id;
    @Column(name = "unique_code", unique = true)
    protected String uniqueCode;
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST})
    protected Collection<TelegramUser> telegramUser = new ArrayList<>();

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public void connectTelegramUser(TelegramUser tUser)
    {
        telegramUser.add(tUser);
    }


}
